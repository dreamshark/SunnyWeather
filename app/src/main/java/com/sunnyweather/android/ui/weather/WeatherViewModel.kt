package com.sunnyweather.android.ui.weather

import androidx.lifecycle.*
import com.sunnyweather.android.logic.Repository

class WeatherViewModel : ViewModel() {

    private val locationLiveData = MutableLiveData<String>()

    var id = ""

    var placeName = ""

    val weatherLiveData = Transformations.switchMap(locationLiveData) {
        Repository.refreshWeather(id, placeName)
    }

    fun refreshWeather(location: kotlin.String) {
        locationLiveData.value = id
    }

}