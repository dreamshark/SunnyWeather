package com.sunnyweather.android.logic.network

import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {

    @GET("v2/city/lookup?key=${SunnyWeatherApplication.TOKEN}")
    fun searchPlaces(@Query("location") query: String): Call<PlaceResponse>

}