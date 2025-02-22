package com.example.goevent

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_events_list, container, false)

        // Récupération des éléments UI
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

        // Récupérer la localisation de l'utilisateur
        arguments?.let {
            userLat = it.getDouble("USER_LAT", 0.0)
            userLon = it.getDouble("USER_LON", 0.0)
        }

        // Récupération des événements Firebase
        fetchEventsFromFirebase()

        eventAdapter = EventAdapter(mutableListOf())  // Initialisation de l'adaptateur avec une liste vide
        recyclerView.adapter = eventAdapter  // Associer l'adaptateur au RecyclerView


        // Configuration des boutons
        btnList.setOnClickListener { switchView(true) }
        btnMap.setOnClickListener { switchView(false) }


        return view
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

                eventList = events
                eventAdapter.updateEvents(events)  // Met à jour l'adaptateur
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Erreur Firebase : ${error.message}")
            }
        })
    }


    /**
     * Fonction pour filtrer les événements en fonction de la distance
     */
    private fun filterEventsByLocation() {
        val filteredEvents = eventList.filter {
            val distance = calculateDistance(userLat, userLon, it.location.latitude, it.location.longitude)
            distance <= 50000 // 50 km
        }

        eventAdapter.updateEvents(filteredEvents) // Mettre à jour l'adaptateur
    }

    /**
     * Fonction pour calculer la distance entre deux points GPS
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000 // Rayon de la Terre en mètres
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

}
