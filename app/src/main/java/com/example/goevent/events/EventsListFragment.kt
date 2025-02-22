package com.example.goevent

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goevent.events.Event
import com.example.goevent.events.EventAdapter
import com.google.android.gms.maps.MapView
import com.google.firebase.database.*

class EventsListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var eventAdapter: EventAdapter
    private lateinit var btnList: Button
    private lateinit var btnMap: Button
    private lateinit var viewList: View
    private lateinit var viewMap: View
    private lateinit var mapView: MapView

    private var eventList: MutableList<Event> = mutableListOf()
    private var userLat: Double = 0.0
    private var userLon: Double = 0.0

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_events_list, container, false)


        // Récupération des éléments UI du layout du fragment
        btnList = view.findViewById(R.id.button_view1)
        btnMap = view.findViewById(R.id.button_view2)
        viewList = view.findViewById(R.id.view1)
        viewMap = view.findViewById(R.id.view2)
        mapView = view.findViewById(R.id.mapView)

        // RecyclerView setup
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialisation de Firebase
        database = FirebaseDatabase.getInstance().reference.child("evenements")

        // Récupération (éventuelle) de la localisation passée en arguments
        arguments?.let {
            userLat = it.getDouble("USER_LAT", 0.0)
            userLon = it.getDouble("USER_LON", 0.0)
        }

        // Récupération des événements (ici, j'utilise le filtrage sans auto-fetch de localisation)
        fetchEventsFromFirebase()

        // Initialisation de l'adaptateur avec une liste vide
        eventAdapter = EventAdapter(mutableListOf())
        recyclerView.adapter = eventAdapter

        // Configuration des boutons de switch de vue
        btnList.setOnClickListener { switchView(true) }
        btnMap.setOnClickListener { switchView(false) }


        return view
    }

    /**
     * Exemple de fonction de filtrage (peut être adaptée)
     */
    private fun filterEventsByLocation() {
        val filteredEvents = eventList.filter {
            val distance = calculateDistance(userLat, userLon, it.location.latitude, it.location.longitude)
            distance <= 500000 // 50 km
        }

        Log.d("DEBUG_FILTER", "Nombre d'événements après filtrage : ${filteredEvents.size}")

        // Mettre à jour l'adaptateur avec la liste filtrée
        eventAdapter.updateEvents(filteredEvents)
    }

    /**
     * Calcul de distance entre deux points GPS
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000 // en mètres
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }

    /**
     * Fonction pour switcher entre la vue liste et la vue carte
     */
    private fun switchView(showList: Boolean) {
        if (showList) {
            viewList.visibility = View.VISIBLE
            viewMap.visibility = View.GONE
            btnList.setBackgroundColor(Color.parseColor("#FF7622"))
            btnMap.setBackgroundColor(Color.parseColor("#98A8B8"))
        } else {
            viewList.visibility = View.GONE
            viewMap.visibility = View.VISIBLE
            btnList.setBackgroundColor(Color.parseColor("#98A8B8"))
            btnMap.setBackgroundColor(Color.parseColor("#FF7622"))
        }
    }

    /**
     * Fonction pour récupérer les événements depuis Firebase
     */
    private fun fetchEventsFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = mutableListOf<Event>()
                for (eventSnapshot in snapshot.children) {
                    val event = eventSnapshot.getValue(Event::class.java)
                    if (event != null) {
                        events.add(event)
                    }
                }

                Log.d("DEBUG_FIREBASE", "Nombre d'événements récupérés : ${events.size}")

                if (events.isEmpty()) {
                    Log.e("DEBUG_FIREBASE", "Aucun événement récupéré ! Vérifie Firebase.")
                } else {
                    for (e in events) {
                        Log.d("DEBUG_FIREBASE", "Événement : ${e.name} - ${e.description}")
                    }
                }

                // Mettre à jour la liste des événements
                eventList = events

                // Appliquer le filtrage après la récupération des événements
                filterEventsByLocation()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Erreur Firebase : ${error.message}")
            }
        })
    }

}
