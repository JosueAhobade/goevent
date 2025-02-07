package com.example.goevent.events

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goevent.R
import com.example.goevent.databinding.AccueilEventBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationServices
import java.util.*

class AccueilEvents : AppCompatActivity() {

    private lateinit var binding: AccueilEventBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventList: MutableList<Event>
    private lateinit var database: DatabaseReference

    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Variables pour la localisation de l'utilisateur
    private var userLat: Double = 0.0
    private var userLon: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AccueilEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonView1.setBackgroundColor(Color.parseColor("#FF7622"))
        binding.buttonView2.setBackgroundColor(Color.parseColor("#98A8B8"))

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        eventList = mutableListOf()

        database = FirebaseDatabase.getInstance().reference.child("evenements")

        fetchEventsFromFirebase()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Vérifier la permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        } else {
            // Si la permission est déjà accordée, obtenir la localisation
            getLocation()
        }

        // Écouter le bouton de filtrage
        binding.filterButton.setOnClickListener {
            showFilterDialog()
        }

        // Écouter le bouton de localisation
        binding.locationButton.setOnClickListener {
            getLocation()
        }
    }

    override fun onResume() {
        super.onResume()
        // Initialisation de FusedLocationProviderClient après que l'activité ait été correctement lancée
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Ensuite, obtenir la localisation
        getLocation()
    }

    private fun showFilterDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Filtrer les événements")

        // Layout du dialogue
        val filterView = layoutInflater.inflate(R.layout.dialog_filter, null)

        val seekBar: SeekBar = filterView.findViewById(R.id.seekBar_radius)
        val radiusTextView: TextView = filterView.findViewById(R.id.radius_value)

        // Initialisation de SeekBar pour le rayon (max 50km, progress initial 10km)
        seekBar.max = 50000  // 50 km max
        seekBar.progress = 10000  // 10 km initial
        radiusTextView.text = "Rayon: 10 km"

        // Mettre à jour la valeur de rayon lorsque le SeekBar change
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                radiusTextView.text = "Rayon: ${progress / 1000} km"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        builder.setView(filterView)

        // Action pour appliquer le filtre
        builder.setPositiveButton("Appliquer") { _, _ ->
            val selectedRadius = seekBar.progress
            filterEventsByRadius(selectedRadius)
        }

        // Action pour annuler
        builder.setNegativeButton("Annuler", null)

        builder.show()
    }

    private fun filterEventsByRadius(radius: Int) {
        val filteredEvents = mutableListOf<Event>()
        val radiusInMeters = radius

        for (event in eventList) {
            val eventLat = event.location.latitude
            val eventLon = event.location.longitude
            val distance = calculateDistance(userLat, userLon, eventLat, eventLon)

            if (distance <= radiusInMeters) {
                filteredEvents.add(event)
            }
        }

        recyclerView.adapter = EventAdapter(filteredEvents)
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000 // Radius of the earth in meters
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c // Distance in meters
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    userLat = location.latitude
                    userLon = location.longitude
                    Toast.makeText(this, "Localisation actuelle : $userLat, $userLon", Toast.LENGTH_SHORT).show()
                    filterEventsByLocation()  // Filtrer les événements selon la localisation
                } else {
                    Toast.makeText(this, "Impossible d'obtenir la localisation", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun filterEventsByLocation() {
        // Ici, tu peux filtrer en fonction de la localisation actuelle de l'utilisateur
        val filteredEvents = mutableListOf<Event>()
        for (event in eventList) {
            val eventLat = event.location.latitude
            val eventLon = event.location.longitude
            val distance = calculateDistance(userLat, userLon, eventLat, eventLon)

            if (distance <= 10000) {  // Filtrer dans un rayon de 10 km
                filteredEvents.add(event)
            }
        }

        recyclerView.adapter = EventAdapter(filteredEvents)
    }

    private fun fetchEventsFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventList.clear()
                for (eventSnapshot in snapshot.children) {
                    val event = eventSnapshot.getValue(Event::class.java)
                    if (event != null) {
                        eventList.add(event)
                    }
                }
                recyclerView.adapter = EventAdapter(eventList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error: ${error.message}")
            }
        })
    }

    fun switchView(view: View) {
        binding.view1.visibility = View.GONE
        binding.view2.visibility = View.GONE

        binding.buttonView1.setBackgroundColor(Color.parseColor("#98A8B8"))
        binding.buttonView2.setBackgroundColor(Color.parseColor("#98A8B8"))

        when (view.tag) {
            "view1" -> {
                binding.view1.visibility = View.VISIBLE
                binding.buttonView1.setBackgroundColor(Color.parseColor("#FF7622"))
            }
            "view2" -> {  // Si le tag est "view2"
                binding.view2.visibility = View.VISIBLE
                binding.buttonView2.setBackgroundColor(Color.parseColor("#FF7622"))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                Toast.makeText(this, "Permission refusée", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
