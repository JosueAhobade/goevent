package com.example.goevent.events

data class Event(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val location: Location = Location(),
    val date: String = "",
    val image: String = ""
)


    data class Location(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0
    )

