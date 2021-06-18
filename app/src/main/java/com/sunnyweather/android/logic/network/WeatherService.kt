package com.sunnyweather.android.logic.network

import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.DailyResponse
import com.sunnyweather.android.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherService {

    @GET("v7/weather/now?key=${SunnyWeatherApplication.TOKEN}")
    fun getRealtimeWeather(@Query("location") location: String): Call<RealtimeResponse>

    @GET("v7/weather/3d?key=${SunnyWeatherApplication.TOKEN}")
    fun getDailyWeather(@Query("location") location: String): Call<DailyResponse>

}