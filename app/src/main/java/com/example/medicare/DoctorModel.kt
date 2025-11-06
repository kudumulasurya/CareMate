package com.example.medicare
data class DoctorModel(
    val uid: String, // Doctor's UID
    val imageUrl: String,
    val hospitalName: String,
    val doctorName: String,
    val experience: String,
    val rating: String,
    val fees: String
)