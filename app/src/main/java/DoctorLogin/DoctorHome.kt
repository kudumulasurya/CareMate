package DoctorLogin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicare.Appointment
import com.example.medicare.databinding.FragmentDoctorHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.PriorityQueue

class DoctorHome : Fragment() {

    private var _binding: FragmentDoctorHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PatientCardAdapter

    private val upcomingQueue = PriorityQueue<Appointment>(
        compareBy<Appointment> { it.queueNumber }.thenBy { it.timestamp }
    )
    private val waitingList = mutableListOf<Appointment>()

    private var selectedAppt: Appointment? = null
    private var currentIndex = 0

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseDatabase.getInstance() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDoctorHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Hide bottom nav for full screen
    override fun onResume() {
        super.onResume()
        activity?.findViewById<BottomNavigationView>(
            resources.getIdentifier("bottom_nav", "id", requireContext().packageName)
        )?.isVisible = false
    }

    override fun onPause() {
        super.onPause()
        activity?.findViewById<BottomNavigationView>(
            resources.getIdentifier("bottom_nav", "id", requireContext().packageName)
        )?.isVisible = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = PatientCardAdapter()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.confirmButton.setOnClickListener { confirmPatient() }
        binding.waiting.setOnClickListener { markAsWaiting() }
        binding.btnPlus.setOnClickListener { nextPatient() }
        binding.btnMinus.setOnClickListener { previousPatient() }
        binding.upcomingPatients.setOnClickListener { showUpcomingList() }
        binding.waitinglist.setOnClickListener { showWaitingList() }

        fetchAppointments()
    }

    // ------------------------------------------------
    // Fetch appointments
    // ------------------------------------------------
    private fun fetchAppointments() {
        val user = auth.currentUser ?: return
        val ref = db.getReference("Appointments").child(user.uid)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                upcomingQueue.clear()
                waitingList.clear()

                val now = System.currentTimeMillis()

                for (child in snapshot.children) {
                    val appt = child.getValue(Appointment::class.java)?.copy(
                        appointmentId = child.key ?: ""
                    ) ?: continue

                    when (appt.status) {
                        "Confirmed" -> {} // skip
                        "Waiting" -> {
                            if (appt.waitUntil <= now) {
                                upcomingQueue.add(appt)
                            } else {
                                waitingList.add(appt)
                            }
                        }
                        else -> upcomingQueue.add(appt)
                    }
                }

                val ordered = currentOrderedList()

                currentIndex = 0
                selectedAppt = ordered.getOrNull(0)

                binding.tvCount.text = ordered.size.toString()
                displayCard(selectedAppt)

                adapter.submitList(ordered)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DoctorHome", "Firebase error", error.toException())
            }
        })
    }

    // ------------------------------------------------
    // Show Card Details / Break Message
    // ------------------------------------------------
    private fun displayCard(appt: Appointment?) {
        if (appt == null) {
            binding.appointmentNo.text = "Appointment no : -"
            binding.name.text = "No Patients Right Now"
            binding.phonenumber.text = "Take a short break 😊"
            binding.gender.text = ""
            binding.address.text = ""

            binding.confirmButton.isEnabled = false
            binding.waiting.isEnabled = false
            return
        }

        binding.confirmButton.isEnabled = true
        binding.waiting.isEnabled = true

        binding.appointmentNo.text = "Appointment no : ${appt.queueNumber}"
        binding.name.text = appt.patientName
        binding.phonenumber.text = appt.patientPhoneNumber
        binding.gender.text = appt.patientGender
        binding.address.text = "Address: ${appt.patientAddress}"
    }

    private fun currentOrderedList(): List<Appointment> {
        return upcomingQueue.toList().sortedWith(
            compareBy<Appointment> { it.queueNumber }.thenBy { it.timestamp }
        )
    }

    // ------------------------------------------------
    // Navigation through patients
    // ------------------------------------------------
    private fun nextPatient() {
        val list = currentOrderedList()
        if (list.isEmpty()) return
        currentIndex = (currentIndex + 1).coerceAtMost(list.lastIndex)
        selectedAppt = list[currentIndex]
        displayCard(selectedAppt)
    }

    private fun previousPatient() {
        val list = currentOrderedList()
        if (list.isEmpty()) return
        currentIndex = (currentIndex - 1).coerceAtLeast(0)
        selectedAppt = list[currentIndex]
        displayCard(selectedAppt)
    }

    private fun showUpcomingList() {
        adapter.submitList(currentOrderedList())
    }

    private fun showWaitingList() {
        adapter.submitList(waitingList.sortedBy { it.waitUntil })
    }

    // ------------------------------------------------
    // ACTION: Confirm
    // ------------------------------------------------
    private fun confirmPatient() {
        val user = auth.currentUser ?: return
        val appt = selectedAppt ?: return

        val data = mapOf(
            "status" to "Confirmed",
            "confirmedAt" to System.currentTimeMillis()
        )

        db.getReference("Appointments").child(user.uid).child(appt.appointmentId)
            .updateChildren(data)

        upcomingQueue.remove(appt)

        val list = currentOrderedList()
        selectedAppt = list.getOrNull(0)
        displayCard(selectedAppt)
        binding.tvCount.text = list.size.toString()
        adapter.submitList(list)
    }

    // ------------------------------------------------
    // ACTION: Waiting
    // ------------------------------------------------
    private fun markAsWaiting() {
        val user = auth.currentUser ?: return
        val appt = selectedAppt ?: return

        val newWait = System.currentTimeMillis() + 60 * 60 * 1000 // 1 hour

        val data = mapOf(
            "status" to "Waiting",
            "waitUntil" to newWait
        )

        db.getReference("Appointments").child(user.uid).child(appt.appointmentId)
            .updateChildren(data)

        upcomingQueue.remove(appt)
        waitingList.add(appt.copy(status = "Waiting", waitUntil = newWait))

        val list = currentOrderedList()
        selectedAppt = list.getOrNull(0)
        displayCard(selectedAppt)
        binding.tvCount.text = list.size.toString()
        adapter.submitList(list)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
