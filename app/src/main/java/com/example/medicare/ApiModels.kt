package com.example.medicare

data class ChatRequest(
    val message: String,
    val session_id: String, // Explicitly sending the session ID to the server
    val conversation_history: List<Message>
)

// Response received from your Python model (for the main chat)
data class ChatResponse(
    val response: String, // Must match 'response' field in your Python jsonify() call
    val source: String,
    val timestamp: String,
    val success: Boolean
    // Note: The Python returns 'response', not 'ai_reply'. Updated ChatResponse fields.
)

// Model for history messages retrieved for a session
data class ServerMessage(
    val role: String, // "user" or "assistant"
    val content: String,
    val timestamp: String
)

// Model for a single session summary in the drawer list
data class SessionSummary(
    val session_id: String,
    val preview: String, // Using 'preview' to match your Python get_all_sessions() output
    val last_active: String,
    val created_at: String
)

// Model for the list of all session summaries
data class AllSessionsResponse(
    val sessions: List<SessionSummary>,
    val success: Boolean
)

// Model for loading a specific session's history
data class HistoryResponse(
    val messages: List<ServerMessage>,
    val session_id: String,
    val success: Boolean
)