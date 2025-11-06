package DoctorLogin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicare.Appointment
import com.example.medicare.databinding.FragmentDoctorHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DoctorHome : Fragment() {

    private var _binding: FragmentDoctorHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var patientCardAdapter: PatientCardAdapter
    private val appointmentsList = mutableListOf<Appointment>()
    private var selectedAppointment: Appointment? = null
    private var currentAppointmentIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        patientCardAdapter = PatientCardAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = patientCardAdapter
        }

        binding.confirmButton.setOnClickListener { updateAppointmentStatus("Confirmed") }
        binding.waiting.setOnClickListener { updateAppointmentStatus("Waiting") }
        binding.btnPlus.setOnClickListener { showNextPatient() }
        binding.btnMinus.setOnClickListener { showPreviousPatient() }
        binding.upcomingPatients.setOnClickListener { filterAppointments("Upcoming") }
        binding.waitinglist.setOnClickListener { filterAppointments("Waiting") }

        fetchAppointments()
    }

    private fun fetchAppointments() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val appointmentsRef = FirebaseDatabase.getInstance().getReference("Appointments").child(currentUser.uid)

        appointmentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                appointmentsList.clear()
                for (appointmentSnapshot in snapshot.children) {
                    val appointment = appointmentSnapshot.getValue(Appointment::class.java)
                    if (appointment != null) {
                        appointmentsList.add(appointment)
                    }
                }
                updateQueueCount()
                if (appointmentsList.isNotEmpty()) {
                    currentAppointmentIndex = 0
                    selectedAppointment = appointmentsList[currentAppointmentIndex]
                    selectedAppointment?.let { displayAppointmentDetails(it) }
                }
                filterAppointments("Upcoming")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DoctorHome", "Failed to read value.", error.toException())
            }
        })
    }

    private fun displayAppointmentDetails(appointment: Appointment?) {
        appointment?.let {
            binding.appointmentNo.text = "Appointment no : ${it.queueNumber}"
            binding.name.text = it.patientName
            binding.phonenumber.text = it.patientPhoneNumber
            binding.gender.text = it.patientGender
            binding.address.text = "Address: ${it.patientAddress}"
        }
    }

    private fun updateQueueCount() {
        binding.tvCount.text = appointmentsList.size.toString()
    }

    private fun updateAppointmentStatus(status: String) {
        selectedAppointment?.let { appointment ->
            val currentUser = FirebaseAuth.getInstance().currentUser ?: return
            val appointmentRef = FirebaseDatabase.getInstance().getReference("Appointments")
                .child(currentUser.uid)
                .child(appointment.appointmentId)

            appointmentRef.child("status").setValue(status)
        }
    }

    private fun showNextPatient() {
        if (appointmentsList.isNotEmpty()) {
            currentAppointmentIndex = (currentAppointmentIndex + 1) % appointmentsList.size
            selectedAppointment = appointmentsList[currentAppointmentIndex]
            selectedAppointment?.let { displayAppointmentDetails(it) }
        }
    }

    private fun showPreviousPatient() {
        if (appointmentsList.isNotEmpty()) {
            currentAppointmentIndex = if (currentAppointmentIndex - 1 < 0) {
                appointmentsList.size - 1
            } else {
                currentAppointmentIndex - 1
            }
            selectedAppointment = appointmentsList[currentAppointmentIndex]
            selectedAppointment?.let { displayAppointmentDetails(it) }
        }
    }

    private fun filterAppointments(status: String) {
        val filteredList = appointmentsList.filter { it.status == status }
        patientCardAdapter.submitList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
