package com.example.medicare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

class ReminderAdapter(
    private val context: FragmentActivity,
    private val reminders: List<Reminder>
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    inner class ReminderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.reminderIcon)
        val time: TextView = view.findViewById(R.id.reminderTime)
        val note: TextView = view.findViewById(R.id.reminderNote)
        val toggle: SwitchCompat = view.findViewById(R.id.reminderSwitch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reminder_item, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.time.text = reminder.time
        holder.note.text = reminder.name
        holder.toggle.isChecked = reminder.isActive

        // Apply initial tint state
        updateSwitchColor(holder.toggle, reminder.isActive)

        holder.toggle.setOnCheckedChangeListener { _, isChecked ->
            reminder.isActive = isChecked
            updateSwitchColor(holder.toggle, isChecked)
        }
    }

    override fun getItemCount(): Int = reminders.size

    private fun updateSwitchColor(switch: SwitchCompat, isChecked: Boolean) {
        if (isChecked) {
            switch.trackDrawable.setTint(ContextCompat.getColor(context, R.color.track_on))
            switch.thumbDrawable.setTint(ContextCompat.getColor(context, R.color.thumb_on))
        } else {
            switch.trackDrawable.setTint(ContextCompat.getColor(context, R.color.track_off))
            switch.thumbDrawable.setTint(ContextCompat.getColor(context, R.color.thumb_off))
        }
    }
}
