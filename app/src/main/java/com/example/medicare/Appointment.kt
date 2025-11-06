package com.example.medicare

data class Appointment(
    val appointmentId: String = "",
    val patientUid: String = "",
    val doctorUid: String = "",
    val patientName: String = "",
    val patientPhoneNumber: String = "",
    val patientGender: String = "",
    val patientAddress: String = "",
    val hospitalName: String? = "",
    val doctorName: String? = "",
    val timestamp: Long = 0,
    var status: String = "Upcoming",
    val queueNumber: Int = 0
)
