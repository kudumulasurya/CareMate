package com.example.medicare

import DoctorLogin.Logindoc
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        auth = Firebase.auth

        val emailEditText = findViewById<TextInputEditText>(R.id.Email)
        val passwordEditText = findViewById<TextInputEditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.button)
        val signupText = findViewById<TextView>(R.id.signupText)
        val forgotPasswordText = findViewById<TextView>(R.id.textView7)
        val doctorLogin = findViewById<TextView>(R.id.doctorlogin)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            if (validateInput(email, password)) {
                loginUser(email, password)
            }
        }

        signupText.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
            finish()
        }

        forgotPasswordText.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email first", Toast.LENGTH_SHORT).show()
            } else {
                sendPasswordResetEmail(email)
            }
        }

        doctorLogin.setOnClickListener {
            startActivity(Intent(this, Logindoc::class.java))
            finish()
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Check if this user is under Patients node in Realtime DB
                        val patientDb = FirebaseDatabase.getInstance()
                            .reference.child("Users").child("Patients").child(user.uid)
                        patientDb.get().addOnSuccessListener { snapshot ->
                            if (snapshot.exists()) {
                                // User is a registered patient, proceed to home
                                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                                navigateToHome()
                            } else {
                                // User is not in Patients database
                                auth.signOut()
                                Toast.makeText(this, "Only registered patients can login here.", Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener {
                            // Database query failed
                            auth.signOut()
                            Toast.makeText(this, "Error verifying patient credentials", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val errorMessage = when {
                        task.exception?.message?.contains("There is no user record") == true -> "No account found with this email"
                        task.exception?.message?.contains("password is invalid") == true -> "Incorrect password"
                        else -> "Login failed: ${task.exception?.message}"
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent to $email", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToHome() {
        val intent = Intent(this, Navigation::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
