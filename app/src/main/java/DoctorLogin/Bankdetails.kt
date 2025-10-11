package DoctorLogin

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.medicare.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Bankdetails : AppCompatActivity() {

    private lateinit var accountholdername: TextInputEditText
    private lateinit var accountnumber: TextInputEditText
    private lateinit var bankname: TextInputEditText
    private lateinit var branchname: TextInputEditText
    private lateinit var ifsccode: TextInputEditText
    private lateinit var bankaddress: TextInputEditText
    private lateinit var accounttype: TextInputEditText
    private lateinit var button: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var doctorlogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bankdetails)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        accountholdername = findViewById(R.id.Accountholdername)
        accountnumber = findViewById(R.id.accountnumber)
        bankname = findViewById(R.id.Bankname)
        branchname = findViewById(R.id.Branchname)
        ifsccode = findViewById(R.id.ifsccode)
        bankaddress = findViewById(R.id.bankaddress)
        accounttype = findViewById(R.id.accounttype)
        button = findViewById(R.id.button)
        progressBar = findViewById(R.id.progressBar)
        doctorlogin = findViewById(R.id.doctorlogin)

        button.setOnClickListener { saveBankDetails() }
        doctorlogin.setOnClickListener {
            startActivity(Intent(this, Logindoc::class.java))
            finish()
        }
    }

    private fun saveBankDetails() {
        val name = accountholdername.text.toString().trim()
        val number = accountnumber.text.toString().trim()
        val bank = bankname.text.toString().trim()
        val branch = branchname.text.toString().trim()
        val ifsc = ifsccode.text.toString().trim()
        val address = bankaddress.text.toString().trim()
        val type = accounttype.text.toString().trim()

        if (name.isEmpty() || number.isEmpty() || bank.isEmpty()
            || branch.isEmpty() || ifsc.isEmpty() || address.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val doctorMap = intent.getSerializableExtra("doctorMap") as? HashMap<String, String>
        if (doctorMap == null) {
            Toast.makeText(this, "Error: Missing doctor details", Toast.LENGTH_SHORT).show()
            return
        }

        doctorMap["accountholdername"] = name
        doctorMap["accountnumber"] = number
        doctorMap["bankname"] = bank
        doctorMap["branchname"] = branch
        doctorMap["ifsccode"] = ifsc
        doctorMap["bankaddress"] = address
        doctorMap["accounttype"] = type
        doctorMap["timestamp"] = System.currentTimeMillis().toString()

        val email = doctorMap["email"] ?: ""
        val password = doctorMap["password"] ?: ""

        progressBar.isVisible = true

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: return@addOnSuccessListener
                doctorMap["uid"] = uid
                FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child("Doctors")
                    .child(uid)
                    .setValue(doctorMap)
                    .addOnSuccessListener {
                        progressBar.isVisible = false
                        Toast.makeText(this, "Doctor signup complete!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, DoctorHomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        progressBar.isVisible = false
                        Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { exception ->
                progressBar.isVisible = false
                Toast.makeText(this, "Signup failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
