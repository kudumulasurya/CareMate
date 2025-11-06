package com.example.medicare

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

private const val REQUEST_CODE_SPEECH_INPUT = 100
private const val TAG = "ChatbotActivity"

class ChatbotActivity : AppCompatActivity() {

    // UI Views
    private lateinit var messageInputEditText: EditText
    private lateinit var sendButton: ImageView
    private lateinit var voiceInputIcon: ImageView
    private lateinit var chatMessagesRecyclerView: RecyclerView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuIcon: ImageView
    private lateinit var historyRecyclerView: RecyclerView

    // Data and Adapter
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private val chatHistory = mutableListOf<Message>()
    private val savedSessions = mutableListOf<SessionSummary>()
    private val apiService = RetrofitClient.instance

    // Current session ID
    private var currentSessionId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_chatbot)

        // 1. Initialize Views
        messageInputEditText = findViewById(R.id.message_input_edit_text)
        sendButton = findViewById(R.id.send_button)
        voiceInputIcon = findViewById(R.id.voice_input_icon)
        chatMessagesRecyclerView = findViewById(R.id.chat_messages_recycler_view)
        menuIcon = findViewById(R.id.menu_icon)
        drawerLayout = findViewById(R.id.drawer_layout)

        val navView: NavigationView = findViewById(R.id.nav_view)
        historyRecyclerView = navView.findViewById(R.id.history_recycler_view)

        // 2. Setup Chat RecyclerView
        chatAdapter = ChatAdapter(chatHistory)
        chatMessagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatbotActivity)
            adapter = chatAdapter
        }

        // 3. Setup History RecyclerView
        historyAdapter = HistoryAdapter(savedSessions) { sessionSummary ->
            loadSession(sessionSummary.session_id ?: "")
        }
        historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatbotActivity)
            adapter = historyAdapter
        }

        // 4. Start a new chat or resume if necessary
        startNewChatSession()

        // 5. Setup Listeners
        sendButton.setOnClickListener { sendMessage() }
        voiceInputIcon.setOnClickListener { startSpeechToText() }

        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
            loadHistorySummaries() // Fetch fresh data from the server
        }

        messageInputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }
    }

    // --- SESSION CONTROL ---

    private fun startNewChatSession() {
        apiService.newChat().enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful && response.body() != null) {
                    currentSessionId = response.body()!!["session_id"] ?: ""
                    chatHistory.clear()
                    chatAdapter.notifyDataSetChanged()
                    chatAdapter.addMessage(Message("Hello! I am your Medical AI Assistant. How can I help you?", isUser = false))
                    Toast.makeText(this@ChatbotActivity, "New chat started.", Toast.LENGTH_SHORT).show()
                } else {
                    handleError("Failed to start new session on server.")
                }
            }
            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                handleError("Network error starting new session: ${t.message}")
            }
        })
    }

    private fun loadHistorySummaries() {
        apiService.getSessions().enqueue(object : Callback<AllSessionsResponse> {
            override fun onResponse(call: Call<AllSessionsResponse>, response: Response<AllSessionsResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    savedSessions.clear()
                    response.body()?.sessions?.let { savedSessions.addAll(it) }
                    historyAdapter.notifyDataSetChanged()
                } else {
                    Log.e(TAG, "Error fetching sessions: ${response.code()}")
                    Toast.makeText(this@ChatbotActivity, "Failed to load chat history.", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<AllSessionsResponse>, t: Throwable) {
                handleError("Network error loading history: ${t.message}")
            }
        })
    }

    private fun loadSession(sessionId: String) {
        if (sessionId.isEmpty()) {
            Toast.makeText(this, "Error: Invalid session ID.", Toast.LENGTH_SHORT).show()
            return
        }
        apiService.loadSessionHistory(sessionId).enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(call: Call<HistoryResponse>, response: Response<HistoryResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    currentSessionId = sessionId
                    chatHistory.clear()
                    response.body()?.messages?.forEach { serverMsg ->
                        val isUser = serverMsg.role == "user"
                        val messageContent = serverMsg.content ?: ""
                        chatHistory.add(Message(messageContent, isUser))
                    }
                    chatAdapter.notifyDataSetChanged()
                    chatMessagesRecyclerView.scrollToPosition(
                        if (chatHistory.isNotEmpty()) chatHistory.size - 1 else 0
                    )
                    drawerLayout.closeDrawer(GravityCompat.START)
                    Toast.makeText(this@ChatbotActivity, "Loaded session: ${sessionId.take(8)}...", Toast.LENGTH_SHORT).show()
                } else {
                    handleError("Failed to load session details from server.")
                }
            }
            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                handleError("Network error loading session: ${t.message}")
            }
        })
    }

    // --- Message Logic ---
    private fun sendMessage() {
        val messageText = messageInputEditText.text.toString().trim()
        if (messageText.isNotEmpty() && currentSessionId.isNotEmpty()) {
            val userMessage = Message(messageText, isUser = true)
            chatHistory.add(userMessage)
            chatAdapter.notifyItemInserted(chatHistory.size - 1)
            chatMessagesRecyclerView.scrollToPosition(chatHistory.size - 1)
            messageInputEditText.setText("")
            getAiResponse(messageText)
        } else if (currentSessionId.isEmpty()) {
            Toast.makeText(this, "Please wait, chat session is initializing.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAiResponse(userQuery: String) {
        val request = ChatRequest(
            message = userQuery,
            session_id = currentSessionId,
            conversation_history = chatHistory
        )
        apiService.getAiResponse(request).enqueue(object : Callback<ChatResponse> {
            override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val aiReply = response.body()!!.response ?: "Sorry, I received an empty response."
                    val aiMessage = Message(aiReply, isUser = false)
                    chatHistory.add(aiMessage)
                    chatAdapter.notifyItemInserted(chatHistory.size - 1)
                    chatMessagesRecyclerView.scrollToPosition(chatHistory.size - 1)
                } else {
                    handleError("Error: API call failed with code ${response.code()}")
                }
            }
            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                handleError("Connection error. Please check your network and server status.")
                t.printStackTrace()
            }
        })
    }

    // --- Utility ---
    private fun handleError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        chatAdapter.addMessage(Message("Sorry, connection failed: $message", isUser = false))
        chatMessagesRecyclerView.scrollToPosition(chatHistory.size - 1)
    }

    // --- Speech to Text ---
    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your medical question...")
        }
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(this, "Speech input not supported on this device.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (result != null && result.isNotEmpty()) {
                messageInputEditText.setText(result[0])
            }
        }
    }
}