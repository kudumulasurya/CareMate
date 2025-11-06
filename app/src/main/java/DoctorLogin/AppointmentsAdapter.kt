package DoctorLogin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.Appointment
import com.example.medicare.R

class AppointmentsAdapter(
    private var appointments: List<Appointment>,
    private val onItemClick: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.bind(appointment)
        holder.itemView.setOnClickListener { onItemClick(appointment) }
    }

    override fun getItemCount(): Int = appointments.size

    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val patientNameTextView: TextView = itemView.findViewById(R.id.patient_name)
        private val appointmentStatusTextView: TextView = itemView.findViewById(R.id.appointment_status)

        fun bind(appointment: Appointment) {
            patientNameTextView.text = appointment.patientName
            appointmentStatusTextView.text = appointment.status
        }
    }

    fun updateList(newList: List<Appointment>) {
        val diffResult = DiffUtil.calculateDiff(AppointmentDiffCallback(this.appointments, newList))
        this.appointments = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class AppointmentDiffCallback(
        private val oldList: List<Appointment>,
        private val newList: List<Appointment>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].appointmentId == newList[newItemPosition].appointmentId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
