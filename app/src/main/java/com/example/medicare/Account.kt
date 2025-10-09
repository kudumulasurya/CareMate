package com.example.medicare

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class Account : Fragment() {
    @SuppressLint("MissingInflatedId", "UseSwitchCompatOrMaterialCode")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        val switch = view.findViewById<Switch>(R.id.switchNotifications)

        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switch.trackDrawable.setTint(ContextCompat.getColor(requireContext(), R.color.track_on))
                switch.thumbDrawable.setTint(ContextCompat.getColor(requireContext(), R.color.thumb_on))
            } else {
                switch.trackDrawable.setTint(ContextCompat.getColor(requireContext(), R.color.track_off))
                switch.thumbDrawable.setTint(ContextCompat.getColor(requireContext(), R.color.thumb_off))
            }
        }

        return view
    }
}
