package DoctorLogin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.medicare.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class DoctorHomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_doctor_home)

        bottomNavigationView = findViewById(R.id.doctorBottomNav)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    replaceFragment(DoctorHome())
                    true
                }

                R.id.patients -> {
                    replaceFragment(Queue())   // create this fragment
                    true
                }

                R.id.profile -> {
                    replaceFragment(DoctorProfile())
                    true
                }

                else -> false
            }
        }

        // Default
        replaceFragment(DoctorHome())
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, fragment)
            .commit()
    }
}
