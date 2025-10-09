package com.example.medicare

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class Documents : Fragment(), DocumentAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    private lateinit var adapter: DocumentAdapter
    private val documentList = mutableListOf<DocumentItem>()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_documents, container, false)

        recyclerView = view.findViewById(R.id.documentRecycler)
        emptyTextView = view.findViewById(R.id.emptyTextView)

        adapter = DocumentAdapter(documentList, this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        updateEmptyView()

        return view
    }

    fun addImage(uri: Uri) {
        val input = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Enter Document Name")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val name = input.text.toString().ifEmpty { "Untitled Document" }
                val timeStamp = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(Date())

                val type = if (uri.toString().lowercase().endsWith(".pdf")) "pdf" else "image"
                val document = DocumentItem(uri, name, timeStamp, type)

                documentList.add(0, document)
                adapter.notifyItemInserted(0)
                recyclerView.scrollToPosition(0)
                updateEmptyView()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onItemClick(document: DocumentItem) {
        val intent = if (document.type == "image") {
            Intent(requireContext(), ImagePreviewActivity::class.java).apply {
                putExtra("imageUri", document.uri.toString())
            }
        } else {
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(document.uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
        }
        startActivity(intent)
    }

    private fun updateEmptyView() {
        if (documentList.isEmpty()) {
            emptyTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}
