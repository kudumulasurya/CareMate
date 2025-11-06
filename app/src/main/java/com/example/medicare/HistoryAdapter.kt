package com.example.medicare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter(
    private val sessions: List<SessionSummary>,
    private val listener: (SessionSummary) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.SessionViewHolder>() {

    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_summary, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.bind(sessions[position], listener)
    }

    override fun getItemCount(): Int = sessions.size

    inner class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.history_title)
        private val snippet: TextView = itemView.findViewById(R.id.history_snippet)
        private val date: TextView = itemView.findViewById(R.id.history_date)

        fun bind(session: SessionSummary, listener: (SessionSummary) -> Unit) {
            title.text = session.preview?.takeIf { it.isNotBlank() }?.take(30) ?: "Untitled"
            snippet.text = "Last active: ${session.last_active?.take(19) ?: "N/A"}"
            date.text = session.created_at?.take(10) ?: "No date"
            itemView.setOnClickListener { listener(session) }
        }
    }
}
