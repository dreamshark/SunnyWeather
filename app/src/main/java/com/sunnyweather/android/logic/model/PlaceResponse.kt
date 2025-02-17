package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName

data class PlaceResponse(val code: String, val location: List<Place>,val refer: Refer)

data class Place(val name: String, val id: String,val lat: String,val lon: String,val adm2: String,val adm1: String,val country: String,val tz: String,val utcOffset: String,val isDst: String,val type: String,val rank: String,val fxLink: String)

data class Refer(val sources: List<String>?,val license: List<String>?)