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

        // Récupération des données de l'intent
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val date = intent.getStringExtra("date")
        val distance = intent.getStringExtra("distance")
        val address = intent.getStringExtra("address")
        val imageRes = intent.getIntExtra("imageRes", R.mipmap.img_autre)

        // Affectation aux vues
        findViewById<TextView>(R.id.detailTitle).text = title
        findViewById<TextView>(R.id.detailDescription).text = description
        findViewById<TextView>(R.id.detailDate).text = "Date : $date"
        findViewById<TextView>(R.id.detailDistance).text = "Distance : $distance"
        findViewById<TextView>(R.id.detailAddress).text = "Adresse : $address"

        // Affectation de l'image
        findViewById<ImageView>(R.id.detailImage).setImageResource(imageRes)

        // Toolbar avec flèche retour
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}
