package com.example.medicare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class Signup : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        auth = Firebase.auth

        val fullName = findViewById<TextInputEditText>(R.id.Name)
        val email = findViewById<TextInputEditText>(R.id.Email)
        val password = findViewById<TextInputEditText>(R.id.password)
        val confirmPassword = findViewById<TextInputEditText>(R.id.confirmpassword)
        val number = findViewById<TextInputEditText>(R.id.Number)
        val signupButton = findViewById<Button>(R.id.button)
        val loginText = findViewById<TextView>(R.id.Login)

        signupButton.setOnClickListener {
            val name = fullName.text.toString().trim()
            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()
            val confirmPasswordText = confirmPassword.text.toString().trim()
            val numberText = number.text.toString().trim()

            // Passwords must match
            if (passwordText != confirmPasswordText) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simple validations
            if (name.isEmpty()) {
                Toast.makeText(this, "Full name is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (passwordText.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!numberText.matches(Regex("\\d{10}"))) {
                Toast.makeText(this, "Enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // All validations passed, register user
            auth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            val userData = User(name, emailText, numberText, "patient")
                            val patientDb = FirebaseDatabase.getInstance()
                                .reference.child("Users").child("Patients")
                            patientDb.child(user.uid).setValue(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, Navigation::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Account created but failed to save profile data", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, Navigation::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                }
                        }
                    } else {
                        val errorMessage = when {
                            task.exception?.message?.contains("email address is already in use") == true ->
                                "Email already in use. Please login instead."
                            task.exception?.message?.contains("password is invalid") == true ->
                                "Password is too weak. Use at least 6 characters."
                            else -> "Sign up failed: ${task.exception?.message}"
                        }
                        Log.e("Signup", errorMessage, task.exception)
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
        }

        loginText.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

    // User data class for Firebase (with role included)
    data class User(val name: String, val email: String, val phone: String, val role: String)
}
