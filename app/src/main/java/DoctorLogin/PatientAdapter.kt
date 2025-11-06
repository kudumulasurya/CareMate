package DoctorLogin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.Appointment
import com.example.medicare.R

class PatientAdapter(private val patientList: List<Appointment>) :
    RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {

    class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val queueNumber: TextView = itemView.findViewById(R.id.tvQueueNumber)
        val patientName: TextView = itemView.findViewById(R.id.tvPatientName)
        val patientPhone: TextView = itemView.findViewById(R.id.tvPatientPhone)
        val status: TextView = itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patient, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patientList[position]
        holder.queueNumber.text = String.format("%02d", patient.queueNumber)
        holder.patientName.text = patient.patientName
        holder.patientPhone.text = patient.patientPhoneNumber
        holder.status.text = patient.status
    }

    override fun getItemCount() = patientList.size
}