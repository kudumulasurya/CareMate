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
        bottomNavigationView = findViewById(R.id.bottom_nav)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home1 -> {
                    replaceFragment(DoctorHome())
                    true
                }
                R.id.patients -> {
                    replaceFragment(Queue())
                    true
                }
                R.id.profile -> {
                    replaceFragment(DoctorProfile())
                    true
                }
                else -> false
            }
        }
        // Default fragment when first loaded
        replaceFragment(DoctorHome())
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame, fragment)
            .commit()
    }
}
