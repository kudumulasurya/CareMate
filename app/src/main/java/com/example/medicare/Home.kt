package com.example.medicare

import android.graphics.Color
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.jvm.java

class Home : Fragment() {

    @SuppressLint("ClickableViewAccessibility", "UseKtx")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val cardView = view.findViewById<CardView>(R.id.card)
        val detailsLayout = view.findViewById<View>(R.id.details_layout)

        val feverImageView = view.findViewById<ImageView>(R.id.fever)
        val coldImageView = view.findViewById<ImageView>(R.id.cold)
        val coughImageView = view.findViewById<ImageView>(R.id.cough)
        val painImageView = view.findViewById<ImageView>(R.id.pain)
        val dentalImageView = view.findViewById<ImageView>(R.id.dental)
        val skinImageView = view.findViewById<ImageView>(R.id.skin)

        detailsLayout.visibility = View.GONE
        fun showDetails() {
            detailsLayout.visibility = View.VISIBLE
            detailsLayout.startAnimation(AlphaAnimation(0f, 1f).apply { duration = 300 })
        }

        fun hideDetails() {
            detailsLayout.startAnimation(AlphaAnimation(1f, 0f).apply { duration = 300 })
            detailsLayout.postDelayed({ detailsLayout.visibility = View.GONE }, 300)
        }

        cardView.setOnClickListener {
            if (detailsLayout.visibility == View.GONE) showDetails() else hideDetails()
        }

        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && detailsLayout.visibility == View.VISIBLE) {
                hideDetails()
            }
            false
        }

        val clickListener = View.OnClickListener { navigateToAppointmentPage() }
        feverImageView.setOnClickListener(clickListener)
        coldImageView.setOnClickListener(clickListener)
        coughImageView.setOnClickListener(clickListener)
        painImageView.setOnClickListener(clickListener)
        dentalImageView.setOnClickListener(clickListener)
        skinImageView.setOnClickListener(clickListener)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val doctorList = mutableListOf(
            DoctorModel(R.drawable.img_10, "Dr. Devaraj Eye Hospital", "Dr. Devaraj M", "Ophthalmologist - 14 years experience", "★★★★★ 45 reviews", "₹300 Consultation Fees"),
            DoctorModel(R.drawable.img_11, "Hope Hospital", "Dr. Krishna Kumar", "Orthopedist - 18 years experience", "★★★★ 31 reviews", "₹400 Consultation Fees"),
            DoctorModel(R.drawable.img_12, "Chisel Dental", "Dr. Deepthi", "Dentist - 21 years experience", "★★★★ 35 reviews", "₹500 Consultation Fees"),
            DoctorModel(R.drawable.img_13, "Children Hospital", "Dr. Mallesh", "Pediatrician - 20 years experience", "★★★★ 20 reviews", "₹300 Consultation Fees")
        )

        val adapter = DoctorAdaptor(doctorList, requireContext())
        recyclerView.adapter = adapter

        return view
    }

    private fun navigateToAppointmentPage() {
        startActivity(Intent(requireContext(), AppointmentActivity::class.java))
    }

}
