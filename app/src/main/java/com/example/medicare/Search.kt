package com.example.medicare

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class Search : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var doctorAdapter: DoctorAdaptor
    private lateinit var allDoctors: List<Doctor>
    private lateinit var searchView: SearchView

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

        allDoctors = emptyList()
        doctorAdapter = DoctorAdaptor(emptyList(), requireContext())

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = doctorAdapter

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
                    allDoctors = snapshot.children.mapNotNull { it.getValue(Doctor::class.java) }
                    doctorAdapter.updateList(allDoctors)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Search", "Failed to read value.", error.toException())
                }
            })
    }

    private fun filter(text: String?) {
        val filteredList = if (text.isNullOrEmpty()) {
            allDoctors
        } else {
            allDoctors.filter {
                it.hospitalName?.contains(text, true) == true ||
                        it.name?.contains(text, true) == true ||
                        it.specialization?.contains(text, true) == true
            }
        }
        doctorAdapter.updateList(filteredList)
    }
}
