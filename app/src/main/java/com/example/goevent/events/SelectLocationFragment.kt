package com.example.goevent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class SelectLocationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_location, container, false)

        val btnSelectLocation = view.findViewById<Button>(R.id.btnSelectLocation)
        btnSelectLocation.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.body_container, EventsListFragment()) // Remplace par le contenu principal après sélection
                .commit()
        }

        return view
    }
}
