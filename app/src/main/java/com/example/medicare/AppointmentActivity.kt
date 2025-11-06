package com.example.medicare

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AppointmentActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_appointment)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Get data from Intent
        val doctorUid = intent.getStringExtra("doctor_uid")
        val doctorImageUrl = intent.getStringExtra("image_url")
        val hospitalName = intent.getStringExtra("hospital_name")
        val doctorName = intent.getStringExtra("doctor_name")
        val specialty = intent.getStringExtra("experience")
        val rating = intent.getStringExtra("rating")
        val fees = intent.getStringExtra("fees")

        // View Binding
        val doctorProfile = findViewById<ImageView>(R.id.ivDoctorProfile)
        val hospital = findViewById<TextView>(R.id.hospital)
        val doctorNameTv = findViewById<TextView>(R.id.docname)
        val specialtyTv = findViewById<TextView>(R.id.specialist)
        val ratingTv = findViewById<TextView>(R.id.rating)
        val feesTv = findViewById<TextView>(R.id.tvConsultationFee)
        val changeDoctorBtn = findViewById<Button>(R.id.btnChangeDoctor)
        val backBtn = findViewById<ImageView>(R.id.Back)
        val paymentBtn = findViewById<Button>(R.id.btnProceedToPayment)

        val patientNameInput = findViewById<TextInputEditText>(R.id.patient)
        val patientPhoneInput = findViewById<TextInputEditText>(R.id.patientPhoneNumber)
        val patientGenderInput = findViewById<TextInputEditText>(R.id.gender)
        val patientAddressInput = findViewById<TextInputEditText>(R.id.address)

        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        changeDoctorBtn.setOnClickListener {
            finish()
        }

        // Set Doctor Info
        Glide.with(this)
            .load(doctorImageUrl)
            .placeholder(R.drawable.ic_doctor_placeholder)
            .error(R.drawable.ic_doctor_placeholder)
            .into(doctorProfile)

        hospital.text = hospitalName
        doctorNameTv.text = doctorName
        specialtyTv.text = specialty
        ratingTv.text = rating
        feesTv.text = fees

        // Handle Appointment Booking
        paymentBtn.setOnClickListener {
            val patientName = patientNameInput.text.toString().trim()
            val patientPhone = patientPhoneInput.text.toString().trim()
            val patientGender = patientGenderInput.text.toString().trim()
            val patientAddress = patientAddressInput.text.toString().trim()
            val patientUid = auth.currentUser?.uid

            if (patientName.isEmpty() || patientPhone.isEmpty() || patientGender.isEmpty() || patientAddress.isEmpty()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (patientUid == null) {
                Toast.makeText(this, "You must be logged in to book.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (doctorUid == null) {
                Toast.makeText(this, "Error: Doctor not found.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            bookAppointment(doctorUid, patientUid, patientName, patientPhone, patientGender, patientAddress, hospitalName, doctorName)
        }
    }

    private fun bookAppointment(
        doctorUid: String,
        patientUid: String,
        patientName: String,
        patientPhone: String,
        patientGender: String,
        patientAddress: String,
        hospitalName: String?,
        doctorName: String?
    ) {
        val appointmentsRef = database.getReference("Appointments").child(doctorUid)

        appointmentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Calculate queue number
                val queueNumber = snapshot.childrenCount.toInt() + 1

                val appointmentId = appointmentsRef.push().key
                if (appointmentId == null) {
                    Toast.makeText(this@AppointmentActivity, "Could not create appointment.", Toast.LENGTH_SHORT).show()
                    return
                }

                val appointment = Appointment(
                    appointmentId = appointmentId,
                    patientUid = patientUid,
                    doctorUid = doctorUid,
                    patientName = patientName,
                    patientPhoneNumber = patientPhone,
                    patientGender = patientGender,
                    patientAddress = patientAddress,
                    hospitalName = hospitalName,
                    doctorName = doctorName,
                    timestamp = System.currentTimeMillis(),
                    status = "Upcoming",
                    queueNumber = queueNumber
                )

                appointmentsRef.child(appointmentId).setValue(appointment)
                    .addOnSuccessListener {
                        Toast.makeText(this@AppointmentActivity, "Appointment Booked Successfully!", Toast.LENGTH_LONG).show()
                        finish() // Go back to the doctor list
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@AppointmentActivity, "Failed to book appointment. Please try again.", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AppointmentActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
