package com.example.medicare

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class Account : Fragment() {

    @SuppressLint("MissingInflatedId", "UseSwitchCompatOrMaterialCode")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        val switch = view.findViewById<Switch>(R.id.switchNotifications)
        val logoutBtn = view.findViewById<TextView>(R.id.logout)

        // 🔘 Switch Color Change Logic
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switch.trackDrawable.setTint(ContextCompat.getColor(requireContext(), R.color.track_on))
                switch.thumbDrawable.setTint(ContextCompat.getColor(requireContext(), R.color.thumb_on))
            } else {
                switch.trackDrawable.setTint(ContextCompat.getColor(requireContext(), R.color.track_off))
                switch.thumbDrawable.setTint(ContextCompat.getColor(requireContext(), R.color.thumb_off))
            }
        }

        // 🔐 Logout Logic
        logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(requireContext(), Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }
}
