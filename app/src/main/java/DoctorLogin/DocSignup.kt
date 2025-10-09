package DoctorLogin

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.medicare.R
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class DocSignup : AppCompatActivity() {

    private lateinit var nameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var numberInput: TextInputEditText
    private lateinit var addressInput: TextInputEditText
    private lateinit var dobInput: TextInputEditText
    private lateinit var genderInput: AutoCompleteTextView
    private lateinit var nextButton: Button
    private lateinit var loginText: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doc_signup)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        nameInput = findViewById(R.id.Name)
        emailInput = findViewById(R.id.Email)
        passwordInput = findViewById(R.id.password)
        confirmPasswordInput = findViewById(R.id.confirmpassword)
        numberInput = findViewById(R.id.Number)
        addressInput = findViewById(R.id.address)
        dobInput = findViewById(R.id.dob)
        genderInput = findViewById(R.id.gender)
        nextButton = findViewById(R.id.button)
        loginText = findViewById(R.id.Login)
        progressBar = findViewById(R.id.progressBar)

        val genderOptions = listOf("Male", "Female", "Other")
        genderInput.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, genderOptions))

        dobInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dpd = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val dobText = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                    dobInput.setText(dobText)
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            )
            dpd.datePicker.maxDate = System.currentTimeMillis()
            dpd.show()
        }

        nextButton.setOnClickListener { validateAndContinue() }
        loginText.setOnClickListener {
            startActivity(Intent(this, Logindoc::class.java))
            finish()
        }
    }

    private fun validateAndContinue() {
        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        val confirmPassword = confirmPasswordInput.text.toString().trim()
        val phone = numberInput.text.toString().trim()
        val address = addressInput.text.toString().trim()
        val dob = dobInput.text.toString().trim()
        val gender = genderInput.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
            phone.isEmpty() || address.isEmpty() || dob.isEmpty() || gender.isEmpty()
        ) {
            showToast("Please fill all fields")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Invalid Email")
            return
        }
        if (password.length < 6) {
            showToast("Password must be at least 6 characters")
            return
        }
        if (password != confirmPassword) {
            showToast("Passwords do not match")
            return
        }

        // Collect data in HashMap, including password (for registration at the end)
        val doctorMap = hashMapOf(
            "name" to name,
            "email" to email,
            "password" to password,
            "phone" to phone,
            "address" to address,
            "dob" to dob,
            "gender" to gender,
            "role" to "Doctor"
        )

        val intent = Intent(this, Doctordetails::class.java)
        intent.putExtra("doctorMap", doctorMap)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
