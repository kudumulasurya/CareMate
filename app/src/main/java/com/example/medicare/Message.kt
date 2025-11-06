package com.example.medicare

data class Message(
    val content: String? = "",
    val isUser: Boolean, // true for user message, false for AI message
    val timestamp: Long = System.currentTimeMillis()
)