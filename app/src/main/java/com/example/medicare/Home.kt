package com.example.medicare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Home : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var doctorAdapter: DoctorAdaptor
    private lateinit var chatbotIcon: ImageView // Declare ImageView for the chatbot

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize RecyclerView components
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        doctorAdapter = DoctorAdaptor(emptyList(), requireContext())
        recyclerView.adapter = doctorAdapter

        // Initialize the Chatbot ImageView and set the click listener
        chatbotIcon = view.findViewById(R.id.chatbot_icon)
        chatbotIcon.setOnClickListener {
            // Launch the ChatbotActivity when the icon is clicked
            val intent = Intent(requireContext(), ChatbotActivity::class.java)
            startActivity(intent)
        }

        fetchDoctorsFromFirebase()
        return view
    }

    private fun fetchDoctorsFromFirebase() {
        FirebaseDatabase.getInstance().getReference("Users").child("Doctors")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val doctorList = snapshot.children.mapNotNull {
                        val doctor = it.getValue(Doctor::class.java)
                        Log.d("HomeFragment", "Fetched doctor: ${doctor?.name}, Image URL: ${doctor?.profileImageUrl}")
                        doctor
                    }
                    doctorAdapter.updateList(doctorList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HomeFragment", "Failed to fetch doctors: ${error.message}")
                }
            })
    }
}