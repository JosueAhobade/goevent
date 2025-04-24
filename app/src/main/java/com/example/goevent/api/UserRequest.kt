package com.example.goevent.api

data class UserRequest(
    val email: String,
    val password: String,
    val username: String? = null
)