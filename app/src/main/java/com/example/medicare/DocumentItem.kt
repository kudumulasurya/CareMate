package com.example.medicare

import android.net.Uri

data class DocumentItem(
    val uri: Uri,
    val name: String,
    val timeStamp: String,
    val type: String
)
