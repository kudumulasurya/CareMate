package com.example.medicare

import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class AppointmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val doctorImageUrl = intent.getStringExtra("image_url")
        val hospitalName = intent.getStringExtra("hospital_name")
        val doctorName = intent.getStringExtra("doctor_name")
        val specialty = intent.getStringExtra("experience")
        val rating = intent.getStringExtra("rating")
        val fees = intent.getStringExtra("fees")

        val DoctorProfile = findViewById<ImageView>(R.id.ivDoctorProfile)
        val Hospital = findViewById<TextView>(R.id.hospital)
        val DoctorName = findViewById<TextView>(R.id.docname)
        val Specialty = findViewById<TextView>(R.id.specialist)
        val Rating = findViewById<TextView>(R.id.rating)
        val Fees = findViewById<TextView>(R.id.tvConsultationFee)
        val ChangeDoctor = findViewById<Button>(R.id.btnChangeDoctor)
        val Back = findViewById<ImageView>(R.id.Back)
        val payment = findViewById<Button>(R.id.btnProceedToPayment)

        Back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        payment.setOnClickListener{
            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
        }

        Glide.with(this)
            .load(doctorImageUrl)
            .placeholder(R.drawable.ic_doctor_placeholder)
            .error(R.drawable.ic_doctor_placeholder)
            .into(DoctorProfile)

        Hospital.text = hospitalName
        DoctorName.text = doctorName
        Specialty.text = specialty
        Rating.text = rating
        Fees.text = fees

        ChangeDoctor.setOnClickListener {
            finish()
        }
    }
}
