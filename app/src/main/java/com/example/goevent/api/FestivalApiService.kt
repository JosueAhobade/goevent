package com.example.goevent.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FestivalApiService {
    @GET("festivals-global-festivals-_-pl/records")
    fun getFestivals(@Query("limit") limit: Int = 20): Call<FestivalResponse>
}