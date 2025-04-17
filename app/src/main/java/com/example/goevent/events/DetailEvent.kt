package com.example.goevent.events

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.goevent.R

class DetailEvent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val date = intent.getStringExtra("date")
        val distance = intent.getStringExtra("distance")
        val address = intent.getStringExtra("address")

        val titleText = findViewById<TextView>(R.id.detailTitle)
        val descriptionText = findViewById<TextView>(R.id.detailDescription)
        val dateText = findViewById<TextView>(R.id.detailDate)
        val distanceText = findViewById<TextView>(R.id.detailDistance)
        val addressText = findViewById<TextView>(R.id.detailAddress)

        titleText.text = title
        descriptionText.text = description
        dateText.text = "Date : $date"
        distanceText.text = "Distance : $distance"
        addressText.text = "Adresse : $address"

        val image = findViewById<ImageView>(R.id.detailImage)
        image.setImageResource(R.drawable.img_test)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}
