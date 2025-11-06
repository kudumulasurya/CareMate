package com.example.medicare

data class ChatSession(
    val sessionId: String,       // Unique ID, e.g., timestamp or UUID
    val title: String,           // Short summary of the conversation
    val lastAccessed: Long,      // Timestamp for sorting
    val summaryText: String,     // First message snippet
    val fullHistoryJson: String
)
