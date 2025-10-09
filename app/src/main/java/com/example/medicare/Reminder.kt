package com.example.medicare

data class Reminder(
    val name: String,
    val type: String,
    val time: String,
    val days: List<String>,
    var isActive: Boolean = true
)
