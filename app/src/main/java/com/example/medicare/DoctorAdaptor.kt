package com.example.medicare

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class DoctorAdaptor(private var doctorList: List<Doctor>, private val context: Context) :
    RecyclerView.Adapter<DoctorAdaptor.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.doctorimg)
        val hospital: TextView = view.findViewById(R.id.hospital)
        val doctor: TextView = view.findViewById(R.id.doctorname)
        val experience: TextView = view.findViewById(R.id.doctordetails)
        val rating: TextView = view.findViewById(R.id.rating)
        val fees: TextView = view.findViewById(R.id.fees)
        val bookButton: Button = view.findViewById(R.id.book1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = doctorList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doctorItem = doctorList[position]

        Glide.with(context)
            .load(doctorItem.profileImageUrl)
            .placeholder(R.drawable.ic_doctor_placeholder)
            .error(R.drawable.ic_doctor_placeholder)
            .into(holder.image)

        holder.hospital.text = doctorItem.hospitalName
        holder.doctor.text = doctorItem.name
        holder.experience.text = "${doctorItem.specialization} - ${doctorItem.yearsOfExperience} years experience"
        holder.rating.text = "★★★★★ 45 reviews" // Placeholder rating
        holder.fees.text = "₹${doctorItem.consultationFee} Consultation Fees"

        holder.bookButton.setOnClickListener {
            val intent = Intent(context, AppointmentActivity::class.java).apply {
                putExtra("doctor_uid", doctorItem.uid)
                putExtra("doctor_name", doctorItem.name)
                putExtra("hospital_name", doctorItem.hospitalName)
                putExtra("experience", doctorItem.specialization)
                putExtra("rating", "★★★★★ 45 reviews")
                putExtra("fees", "₹${doctorItem.consultationFee} Consultation Fees")
                putExtra("image_url", doctorItem.profileImageUrl)
            }
            context.startActivity(intent)
        }
    }

    fun updateList(newList: List<Doctor>) {
        val diffResult = DiffUtil.calculateDiff(DoctorDiffCallback(this.doctorList, newList))
        this.doctorList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class DoctorDiffCallback(private val oldList: List<Doctor>, private val newList: List<Doctor>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].uid == newList[newItemPosition].uid
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
