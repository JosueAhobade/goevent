package com.example.goevent.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        val dist = event.distance?.toString()

        holder.titleTextView.text = event.nom_du_festival
        holder.descriptionTextView.text = event.discipline_dominante ?: "Pas de description disponible"
        holder.addressTextView.text = event.adresse ?: "Adresse inconnue"
        holder.periodeTextView.text = event.periode_principale_de_deroulement_du_festival ?: "Date inconnnue"
        holder.distanceTextView.text = dist ?: "Distance inconnue"



        holder.heartIcon.setOnClickListener {

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
}
