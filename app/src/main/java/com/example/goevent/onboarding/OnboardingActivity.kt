package com.example.goevent.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.goevent.MainActivity
import com.example.goevent.R
import com.example.goevent.events.AccueilEvents
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val btnNext: Button = findViewById(R.id.btnNext)
        val btnSkip: TextView = findViewById(R.id.btnSkip)

        val layouts = listOf(
            R.layout.page01_onboarding,
            R.layout.page02_onboarding,
            R.layout.page03_onboarding
        )

        val adapter = OnboardingAdapter(layouts)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        btnNext.setOnClickListener {
            if (viewPager.currentItem < layouts.size - 1) {
                viewPager.currentItem = viewPager.currentItem + 1
            } else {
                navigateToNextActivity()
            }
        }
        btnSkip.setOnClickListener {
            navigateToNextActivity()
        }
    }

    private fun navigateToNextActivity() {

        startActivity(Intent(this, AccueilEvents::class.java))
        finish()
    }
}