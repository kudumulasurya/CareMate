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

    // status: "Upcoming", "Waiting", "Confirmed"
    var status: String = "Upcoming",

    // Queue priority (lower = earlier)
    val queueNumber: Int = 0,

    // NEW: when a patient is sent to waiting, they rejoin upcoming after this time
    var waitUntil: Long = 0L,

    // NEW: when confirmed, record time
    var confirmedAt: Long = 0L
)
