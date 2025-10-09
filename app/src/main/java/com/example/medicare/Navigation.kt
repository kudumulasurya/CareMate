package com.example.medicare


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.WindowManager

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Navigation : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility", "UseKtx")
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        setContentView(R.layout.activity_navigation)
        bottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    replaceFragment(Home())
                    true
                }
                R.id.search ->{
                    replaceFragment(Search())
                    true
                }
                R.id.alarm ->{
                    replaceFragment(Documinder())
                    true
                }
                R.id.profile ->{
                    replaceFragment(Account())
                    true
                }

                else -> false
            }

        }
        replaceFragment(Home())


    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame, fragment).commit()
    }

}
