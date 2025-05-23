package com.example.goevent.events

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goevent.R
import com.example.goevent.databinding.AccueilEventBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.*
import android.location.Geocoder
import android.widget.Button
import com.example.goevent.EventsListFragment
import com.example.goevent.auth.Profile
import com.example.goevent.auth.Signin
import java.util.Locale

class AccueilEvents : AppCompatActivity() {

    private lateinit var binding: AccueilEventBinding
    private lateinit var database: DatabaseReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val REQUEST_LOCATION_PERMISSION = 1
    private var userLat: Double = 0.0
    private var userLon: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AccueilEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference.child("evenements")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val btn_profile: Button = binding.buttonProfile

        checkLocationPermission()

        btn_profile.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        } else {
            getLocation()
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("DEBUG_TAG", "Permission de localisation refusée")
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                userLat = location.latitude
                userLon = location.longitude
                Log.d("DEBUG_TAG", "Localisation utilisateur : $userLat, $userLon")

                val geocoder = Geocoder(this, Locale.getDefault())
                try {
                    val addresses = geocoder.getFromLocation(userLat, userLon, 1)
                    val cityName = addresses?.get(0)?.locality ?: "Inconnu"
                    val locationText = "$cityName"

                    binding.textViewLocation.text = "Localisation: $locationText"
                    val fragment = EventsListFragment().apply {
                        arguments = Bundle().apply {
                            putDouble("USER_LAT", userLat)
                            putDouble("USER_LON", userLon)
                        }
                    }

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.body_container, fragment)
                        .commit()

                } catch (e: Exception) {
                    Log.e("DEBUG_TAG", "Erreur localisation", e)
                    binding.textViewLocation.text = "Erreur localisation"
                }
            } else {
                Log.e("DEBUG_TAG", "Localisation non disponible")
                binding.textViewLocation.text = "Localisation non disponible"

                val fragment = EventsListFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.body_container, fragment)
                    .commit()

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
