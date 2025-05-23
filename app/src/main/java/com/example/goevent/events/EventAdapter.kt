package com.example.goevent.events

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.goevent.R
import com.example.goevent.api.Festival

class EventAdapter(private var eventList: MutableList<Festival>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.eventTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.eventDescription)
        val addressTextView: TextView = itemView.findViewById(R.id.addressText)
        val periodeTextView: TextView = itemView.findViewById(R.id.periodeText)
        val heartIcon: ImageView = itemView.findViewById(R.id.heartIcon)
        val distanceTextView: TextView = itemView.findViewById(R.id.distanceText)
        val addressLayout: LinearLayout = itemView.findViewById(R.id.addressLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        val dist = event.distance?.toString()

        // Image selon la discipline
        val imageRes = getImageForDiscipline(event.discipline_dominante)
        holder.itemView.findViewById<ImageView>(R.id.eventImage).setImageResource(imageRes)

        // Infos dans les TextView
        holder.titleTextView.text = event.nom_du_festival
        holder.descriptionTextView.text = event.discipline_dominante ?: "Pas de description disponible"
        holder.addressTextView.text = event.adresse ?: "Adresse inconnue"
        holder.periodeTextView.text = event.periode_principale_de_deroulement_du_festival ?: "Date inconnue"
        holder.distanceTextView.text = if (dist != null) "$dist km" else "Distance inconnue"

        // Clic sur l'adresse pour ouvrir Google Maps
        holder.addressLayout.setOnClickListener {
            val address = holder.addressTextView.text.toString()
            openGoogleMaps(holder.itemView.context, address)
        }

        // Clic sur la carte pour ouvrir les détails
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailEvent::class.java)

            intent.putExtra("imageRes", imageRes)
            intent.putExtra("title", event.nom_du_festival)
            intent.putExtra("description", event.discipline_dominante)
            intent.putExtra("date", event.periode_principale_de_deroulement_du_festival)
            intent.putExtra("distance", dist ?: "Distance inconnue")
            intent.putExtra("address", event.adresse)

            context.startActivity(intent)
        }

        // Clic sur le cœur (à implémenter plus tard)
        holder.heartIcon.setOnClickListener {
            // TODO: ajouter aux favoris ?
        }
    }


    override fun getItemCount(): Int {
        return eventList.size
    }

    fun updateEvents(newEvents: List<Festival>) {
        eventList.clear()
        eventList.addAll(newEvents)
        notifyDataSetChanged()
    }

    /**
     * Fonction pour ouvrir google maps
     */
    private fun openGoogleMaps(context: Context, address: String) {
        if (address.isNotEmpty()) {
            val uri = Uri.encode(address)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$uri"))
            intent.setPackage("com.google.android.apps.maps")

            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("MAP_ERROR", "Google Maps introuvable", e)
                Toast.makeText(context, "Google Maps n'est pas installé", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Adresse non disponible", Toast.LENGTH_SHORT).show()
        }
    }

    private val disciplineImages = mapOf(
        "Musique" to R.mipmap.img_musique,
        "Arts visuels, arts numeriques" to R.mipmap.img_arts,
        "Cinéma, audiovisuel" to R.mipmap.img_cinema,
        "Livre, littérature" to R.mipmap.img_livre,
        "Autre" to R.mipmap.img_autre
    )

    private fun getImageForDiscipline(discipline: String?): Int {
        val cleaned = when {
            discipline == null -> "Autre"
            discipline.contains("musique", true) -> "Musique"
            discipline.contains("art", true) -> "Arts visuels, arts numeriques"
            discipline.contains("cinema", true) -> "Cinéma, audiovisuel"
            discipline.contains("livre", true) -> "Livre, littérature"
            else -> "Autre"
        }

        return disciplineImages[cleaned] ?: R.mipmap.img_autre
    }


}
