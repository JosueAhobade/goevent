package com.example.goevent

import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goevent.api.Festival
import com.example.goevent.api.FestivalResponse
import com.example.goevent.api.RetrofitClient
import com.example.goevent.events.Event
import com.example.goevent.events.EventAdapter
import com.example.goevent.events.CategoryAdapter
import com.google.android.gms.maps.MapView
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class EventsListFragment : Fragment() {

    private lateinit var recyclerViewHorizontal: RecyclerView
    private lateinit var recyclerViewVertical: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var eventAdapter: EventAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var btnList: Button
    private lateinit var btnMap: Button
    private lateinit var viewList: View
    private lateinit var viewMap: View
    private lateinit var mapView: MapView

    private var eventList: MutableList<Festival> = mutableListOf()
    private var categoryList: MutableList<String> = mutableListOf()
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

        // Initialisation des vues
        btnList = view.findViewById(R.id.button_view1)
        btnMap = view.findViewById(R.id.button_view2)
        viewList = view.findViewById(R.id.view1)
        viewMap = view.findViewById(R.id.view2)
        mapView = view.findViewById(R.id.mapView)

        // Initialisation des RecyclerView
        recyclerViewHorizontal = view.findViewById(R.id.recyclerViewHorizontal)
        recyclerViewHorizontal.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        recyclerViewVertical = view.findViewById(R.id.recyclerViewVertical)
        recyclerViewVertical.layoutManager = LinearLayoutManager(requireContext())

        // Initialisation des adaptateurs
        eventAdapter = EventAdapter(mutableListOf())

        categoryAdapter = CategoryAdapter(mutableListOf())
        recyclerViewVertical.adapter = eventAdapter

        // Récupération de Firebase
        database = FirebaseDatabase.getInstance().reference.child("evenements")

        arguments?.let {
            userLat = it.getDouble("USER_LAT", 0.0)
            userLon = it.getDouble("USER_LON", 0.0)
        }

        fetchFestivals(userLat, userLon)

       // fetchEventsFromFirebase()

        // Gestion du changement de vue
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

                //eventList = events

                // Mise à jour des adaptateurs
                eventAdapter.updateEvents(eventList)
                categoryAdapter.updateCategories(categoryList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Erreur Firebase : ${error.message}")
            }
        })
    }

    /**
     * Fonction pour récupérer les événements depuis l'api de data gouv
     */
    private fun fetchFestivals(userLat: Double, userLon: Double) {
        RetrofitClient.instance.getFestivals().enqueue(object : Callback<FestivalResponse> {
            override fun onResponse(call: Call<FestivalResponse>, response: Response<FestivalResponse>) {
                if (!isAdded) return // 🔥 Vérifie si le Fragment est encore attaché

                if (response.isSuccessful) {
                    val events = response.body()?.results
                    if (!events.isNullOrEmpty()) {
                        eventList.clear()

                        // 🔥 Vérifie encore une fois avant d'utiliser requireContext()
                        val geocoder = if (isAdded) Geocoder(requireContext(), Locale.getDefault()) else null

                        events.forEach { event ->
                            val geo = event.geocodage_xy

                            if (geo != null && geo.lat != null && geo.lon != null) {
                                event.distance = calculateDistance(userLat, userLon, geo.lat, geo.lon)

                                try {
                                    // 🔥 Vérifie que le Geocoder est bien initialisé
                                    if (geocoder != null) {
                                        val addresses = geocoder.getFromLocation(geo.lat, geo.lon, 1)
                                        val cityName = addresses?.get(0)?.locality ?: "Inconnu"
                                        val countryName = addresses?.get(0)?.countryName ?: "Inconnu"
                                        val postalCode = addresses?.get(0)?.postalCode ?: "Inconnu"
                                        event.adresse = "$postalCode $cityName, $countryName"
                                    } else {
                                        event.adresse = "Localisation indisponible"
                                    }
                                } catch (e: Exception) {
                                    Log.e("DEBUG_TAG", "Erreur localisation", e)
                                    event.adresse = "Erreur localisation"
                                }
                            } else {
                                Log.e("ERROR_DISTANCE", "Coordonnées GPS manquantes pour ${event.nom_du_festival}")
                            }
                        }

                        eventList.addAll(events)
                        eventAdapter.updateEvents(eventList) // `notifyDataSetChanged()` est déjà dans `updateEvents()`
                    } else {
                        Log.e("API_RESULT", "Aucun festival trouvé.")
                    }
                } else {
                    Log.e("API_ERROR", "Réponse non réussie: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<FestivalResponse>, t: Throwable) {
                Log.e("API_ERROR", "Erreur : ${t.message}")
            }
        })
    }

    /**
     * Fonction pour calculer la distance
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return (earthRadius * c) / 1000
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
