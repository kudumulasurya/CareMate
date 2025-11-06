package DoctorLogin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicare.Appointment
import com.example.medicare.R

class PatientCardAdapter : ListAdapter<Appointment, PatientCardAdapter.PatientCardViewHolder>(PatientCardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_patient_card, parent, false)
        return PatientCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientCardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PatientCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val patientPhoto: ImageView = itemView.findViewById(R.id.patient_photo)
        private val patientName: TextView = itemView.findViewById(R.id.patient_name)
        private val patientPhone: TextView = itemView.findViewById(R.id.patient_phone)
        private val patientGender: TextView = itemView.findViewById(R.id.patient_gender)
        private val patientAddress: TextView = itemView.findViewById(R.id.patient_address)
        private val appointmentNumber: TextView = itemView.findViewById(R.id.appointment_number)

        fun bind(appointment: Appointment) {
            patientName.text = appointment.patientName
            patientPhone.text = appointment.patientPhoneNumber
            patientGender.text = appointment.patientGender
            patientAddress.text = appointment.patientAddress
            appointmentNumber.text = "Appointment no.: ${appointment.queueNumber}"

            // You can add a placeholder for the patient's photo
            Glide.with(itemView.context)
                .load(R.drawable.ic_doctor_placeholder) // Replace with actual patient photo URL if available
                .into(patientPhoto)
        }
    }

    class PatientCardDiffCallback : DiffUtil.ItemCallback<Appointment>() {
        override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
            return oldItem.appointmentId == newItem.appointmentId
        }

        override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
            return oldItem == newItem
        }
    }
}
