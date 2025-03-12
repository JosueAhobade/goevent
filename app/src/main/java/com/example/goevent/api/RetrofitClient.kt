package com.example.goevent.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL=   "https://data.culture.gouv.fr/api/explore/v2.1/catalog/datasets/"

    val instance: FestivalApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FestivalApiService::class.java)
    }
}