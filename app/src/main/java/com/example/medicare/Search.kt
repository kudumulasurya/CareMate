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

        dataList = mutableListOf(
            DoctorModel(R.drawable.img_10, "Dr. Devaraj Eye Hospital", "Dr. Devaraj M", "Ophthalmologist - 14 years experience", "★★★★★ 45 reviews", "₹300 Consultation Fees"),
            DoctorModel(R.drawable.img_11, "Hope Hospital", "Dr. Krishna Kumar", "Orthopedist - 18 years experience", "★★★★ 31 reviews", "₹400 Consultation Fees"),
            DoctorModel(R.drawable.img_12, "Chisel Dental", "Dr. Deepthi", "Dentist - 21 years experience", "★★★★ 35 reviews", "₹500 Consultation Fees"),
            DoctorModel(R.drawable.img_13, "Children Hospital", "Dr. Mallesh", "Pediatrician - 20 years experience", "★★★★ 20 reviews", "₹300 Consultation Fees")
        )

        filteredList = mutableListOf()
        itemAdapter = DoctorAdaptor(filteredList, requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = itemAdapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })

        return view
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