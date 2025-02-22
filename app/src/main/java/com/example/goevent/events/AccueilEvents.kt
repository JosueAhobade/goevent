package com.example.goevent.events

import android.Manifest
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
import com.example.goevent.EventsListFragment
import com.example.goevent.SelectLocationFragment
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

        //checkLocationPermission()
    }

    override fun onResume() {
        super.onResume()
        checkLocationPermission()  // S'assure que la localisation est mise à jour après redémarrage
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        } else {
            getLocation()  // Si la permission est déjà accordée, obtenir la localisation
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
                    val countryName = addresses?.get(0)?.countryName ?: "Inconnu"
                    val locationText = "$cityName, $countryName"

                    binding.textViewLocation.text = "Localisation: $locationText"

                    // Passer la localisation à EventsListFragment
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

                supportFragmentManager.beginTransaction()
                    .replace(R.id.body_container, SelectLocationFragment())
                    .commit()
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()  // Si la permission est accordée, récupérer la localisation
            } else {
                Toast.makeText(this, "Permission refusée", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
