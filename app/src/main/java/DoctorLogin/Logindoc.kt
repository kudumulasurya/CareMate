package DoctorLogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.medicare.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.FirebaseDatabase

class Logindoc : AppCompatActivity() {
    private val TAG = "DoctorLogin"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge-to-edge/fullscreen drawing
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setContentView(R.layout.activity_logindoc)

        val email = findViewById<TextInputEditText>(R.id.Email)
        val password = findViewById<TextInputEditText>(R.id.password)
        val loginBtn = findViewById<Button>(R.id.button)
        val signupLink = findViewById<TextView>(R.id.Login)

        // Signup intent to DocSignup page
        signupLink.setOnClickListener {
            startActivity(Intent(this, DocSignup::class.java))
            finish()
        }

        loginBtn.setOnClickListener {
            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()
            Log.d(TAG, "Login attempt with email: $emailText")

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val auth = Firebase.auth
            auth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        Log.d(TAG, "Firebase auth successful for UID: ${user?.uid}")

                        if (user != null) {
                            val doctorDb = FirebaseDatabase.getInstance()
                                .reference.child("Users").child("Doctors").child(user.uid)
                            Log.d(TAG, "Checking database path: Users/Doctors/${user.uid}")

                            doctorDb.get().addOnSuccessListener { snapshot ->
                                Log.d(TAG, "Database query successful. Exists: ${snapshot.exists()}")
                                Log.d(TAG, "Snapshot data: ${snapshot.value}")

                                if (snapshot.exists()) {
                                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                                    // Navigate to the DoctorHome activity
                                    val intent = Intent(this, DoctorHomeActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Log.w(TAG, "User not found in Doctors database")
                                    auth.signOut()
                                    Toast.makeText(this, "Only registered doctors can login here.", Toast.LENGTH_SHORT).show()
                                }
                            }.addOnFailureListener { exception ->
                                Log.e(TAG, "Database query failed", exception)
                                auth.signOut()
                                Toast.makeText(this, "Error verifying doctor credentials: ${exception.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Log.e(TAG, "Firebase auth failed", task.exception)
                        val errorMessage = when {
                            task.exception?.message?.contains("There is no user record") == true ->
                                "No account found with this email"
                            task.exception?.message?.contains("password is invalid") == true ->
                                "Incorrect password"
                            else -> "Login failed: ${task.exception?.message}"
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
