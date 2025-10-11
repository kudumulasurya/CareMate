package com.example.medicare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlin.random.Random
import kotlin.math.round

class Home : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var doctorAdapter: DoctorAdaptor
    private val doctorList = mutableListOf<DoctorModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        doctorAdapter = DoctorAdaptor(doctorList, requireContext())
        recyclerView.adapter = doctorAdapter

        fetchDoctorsFromFirebase()
        return view
    }

    private fun fetchDoctorsFromFirebase() {
        FirebaseDatabase.getInstance().getReference("Users").child("Doctors")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    doctorList.clear()
                    for (doctorSnap in snapshot.children) {
                        val map = doctorSnap.value as? Map<*, *> ?: continue

                        // Patch http -> https for image URLs!
                        val rawImageUrl = map["profileImageUrl"] as? String ?: ""
                        val imageUrl = if (rawImageUrl.startsWith("http://")) {
                            rawImageUrl.replace("http://", "https://")
                        } else {
                            rawImageUrl
                        }

                        val hospitalName = map["hospitalName"] as? String ?: ""
                        val doctorName = map["name"] as? String ?: ""
                        val specialization = map["specialization"] as? String ?: ""
                        val yearsOfExperience = map["yearsOfExperience"] as? String ?: ""
                        val fee = map["consultationFee"] as? String ?: ""

                        // Random rating value from 3.5 to 5.0 with one decimal
                        val ratingValue = round((Random.nextDouble(3.5, 5.01)) * 10) / 10
                        // Number of stars: round to nearest integer between 3 and 5
                        val starsCount = ratingValue.toInt().coerceIn(3, 5)
                        val ratingStars = "★".repeat(starsCount)
                        // Generate random review count (40 to 100)
                        val reviewCount = Random.nextInt(40, 101)
                        val rating = "$ratingStars  $ratingValue  ($reviewCount reviews)"

                        doctorList.add(
                            DoctorModel(
                                imageUrl = imageUrl,
                                hospitalName = hospitalName,
                                doctorName = doctorName,
                                experience = "$specialization - $yearsOfExperience years experience",
                                rating = rating,
                                fees = "₹$fee Consultation Fees"
                            )
                        )
                    }
                    doctorAdapter.updateList(doctorList)
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
