package DoctorLogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.medicare.Doctor
import com.example.medicare.Login
import com.example.medicare.databinding.FragmentDoctorProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DoctorProfile : Fragment() {

    private var _binding: FragmentDoctorProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DatabaseReference
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorProfileBinding.inflate(inflater, container, false)

        db = FirebaseDatabase.getInstance().getReference("Users").child("Doctors")

        loadDoctorProfile()
        setupLogout()

        return binding.root
    }

    private fun loadDoctorProfile() {
        val currentUID = auth.currentUser?.uid ?: return

        db.child(currentUID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    val doctor = snapshot.getValue(Doctor::class.java)

                    Log.d("DoctorProfile", "Fetched: ${doctor?.name}")
                    binding.tvDoctorName.text = doctor?.name ?: ""
                    binding.tvSpecialization.text = doctor?.specialization ?: ""
                    binding.tvPhone.text = doctor?.phone ?: ""
                    binding.tvEmail.text = doctor?.email ?: ""


                    // Load Cloudinary image
                    if (!doctor?.profileImageUrl.isNullOrEmpty()) {
                        Glide.with(requireContext())
                            .load(doctor!!.profileImageUrl)
                            .into(binding.imgDoctor)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DoctorProfile", "DB Error: ${error.message}")
            }
        })
    }

    private fun setupLogout() {
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(requireContext(), Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
