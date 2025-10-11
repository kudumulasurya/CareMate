package com.example.medicare

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlin.random.Random
import kotlin.math.round

class Search : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: DoctorAdaptor
    private lateinit var dataList: MutableList<DoctorModel>
    private lateinit var searchView: SearchView
    private lateinit var filteredList: MutableList<DoctorModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        searchView = view.findViewById(R.id.search)
        val searchEditText = searchView.findViewById<android.widget.EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(Color.BLACK)
        searchEditText.setHintTextColor(Color.DKGRAY)

        dataList = mutableListOf()
        filteredList = mutableListOf()
        itemAdapter = DoctorAdaptor(filteredList, requireContext())

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = itemAdapter

        fetchDoctorsFromFirebase()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })

        return view
    }

    private fun fetchDoctorsFromFirebase() {
        FirebaseDatabase.getInstance().getReference("Users").child("Doctors")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dataList.clear()
                    for (doctorSnap in snapshot.children) {
                        val map = doctorSnap.value as? Map<*, *> ?: continue

                        // PATCH HTTP to HTTPS!
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
                        
                        val ratingValue = round((Random.nextDouble(3.5, 5.01)) * 10) / 10
                        val starsCount = ratingValue.toInt().coerceIn(3, 5)
                        val ratingStars = "★".repeat(starsCount)
                        val reviewCount = Random.nextInt(40, 101)
                        val rating = "$ratingStars  $ratingValue  ($reviewCount reviews)"

                        dataList.add(
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
                    filteredList.clear()
                    filteredList.addAll(dataList)
                    itemAdapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun filter(text: String?) {
        filteredList.clear()
        if (!text.isNullOrEmpty()) {
            filteredList.addAll(
                dataList.filter {
                    it.hospitalName.contains(text, true) ||
                            it.doctorName.contains(text, true) ||
                            it.experience.contains(text, true)
                }
            )
        } else {
            filteredList.addAll(dataList)
        }
        itemAdapter.notifyDataSetChanged()
    }
}
