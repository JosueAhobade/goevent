package com.example.goevent.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface BackendApiService {
    @POST("signup")
    fun signup(@Body user: UserRequest): Call<UserResponse>

    @POST("login")
    fun login(@Body user: UserRequest): Call<Map<String, Any>>
}