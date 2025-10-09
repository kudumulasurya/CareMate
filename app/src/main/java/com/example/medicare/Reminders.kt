package com.example.medicare

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Reminders : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReminderAdapter
    private val remindersList = mutableListOf<Reminder>()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reminders, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewReminders)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ReminderAdapter(requireActivity(), remindersList)
        recyclerView.adapter = adapter

        val setupLayout = view.findViewById<LinearLayout>(R.id.setupLayout)
        val addReminderBtn = view.findViewById<Button>(R.id.btnAddReminder)
        val doneBtn = view.findViewById<Button>(R.id.btnDone)
        val nameEditText = view.findViewById<EditText>(R.id.etReminderName)
        val spinner = view.findViewById<Spinner>(R.id.spinnerType)
        val timePicker = view.findViewById<TimePicker>(R.id.timePicker)

        val toggleDays = listOf(
            view.findViewById<ToggleButton>(R.id.toggleSunday),
            view.findViewById<ToggleButton>(R.id.toggleMonday),
            view.findViewById<ToggleButton>(R.id.toggleTuesday),
            view.findViewById<ToggleButton>(R.id.toggleWednesday),
            view.findViewById<ToggleButton>(R.id.toggleThursday),
            view.findViewById<ToggleButton>(R.id.toggleFriday),
            view.findViewById<ToggleButton>(R.id.toggleSaturday)
        )

        // Populate spinner
        val types = listOf("Medicine", "Doctor", "Exercise", "Others")
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, types)

        fun clearInputs() {
            nameEditText.setText("")
            spinner.setSelection(0)
            timePicker.hour = 8
            timePicker.minute = 0
            toggleDays.forEach { it.isChecked = false }
        }

        doneBtn.setOnClickListener {
            val name = nameEditText.text.toString()
            val type = spinner.selectedItem.toString()
            val hour = timePicker.hour
            val minute = timePicker.minute
            val time = String.format("%02d:%02d", hour, minute)

            val days = toggleDays.mapIndexedNotNull { index, button ->
                if (button.isChecked) listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")[index] else null
            }

            if (name.isNotBlank()) {
                remindersList.add(0, Reminder(name, type, time, days))
                adapter.notifyItemInserted(0)
                recyclerView.scrollToPosition(0)

                setupLayout.visibility = View.GONE
                addReminderBtn.visibility = View.VISIBLE
                clearInputs()
            } else {
                Toast.makeText(requireContext(), "Please enter a name", Toast.LENGTH_SHORT).show()
            }
        }

        addReminderBtn.setOnClickListener {
            setupLayout.visibility = View.VISIBLE
            addReminderBtn.visibility = View.GONE
        }

        // Check if data is passed for editing
        arguments?.let {
            val name = it.getString("name", "")
            val type = it.getString("type", "")
            val time = it.getString("time", "")
            val days = it.getStringArrayList("days") ?: arrayListOf()

            if (name.isNotEmpty()) {
                remindersList.add(0, Reminder(name, type, time, days))
                adapter.notifyItemInserted(0)
                recyclerView.scrollToPosition(0)
                setupLayout.visibility = View.GONE
                addReminderBtn.visibility = View.VISIBLE
            }
        }

        return view
    }
}