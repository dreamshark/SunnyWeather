package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.String

data class DailyResponse(val code: String,val updateTime: String,val fxLink: String, val daily: List<Daily>,val refer: Refer) {

    data class Daily(val fxDate: Date,val sunrise: String,val sunset: String,val moonrise: String,val moonset: String,val moonPhase: String,val tempMax: Float, val tempMin: Float,val iconDay: String,val textDay: String,val iconNight: String,val textNight: String,val wind360Day: String,val windDirDay: String,val windScaleDay: String,val windSpeedDay: String,val wind360Night: String,val windDirNight: String,val windScaleNight: String,val windSpeedNight: String,val humidity: Int,val precip: Float,val pressure: Int,val vis: Int,val cloud: Int,val uvIndex: Int)
//    data class DailyResponse(val code: String,val updateTime: String,val fxLink: String, val daily: Daily,val refer: Refer) {
//
//        data class Daily(val fxDate: List<Date>,val sunrise: List<String>,val sunset: List<String>,val moonrise: List<String>,val moonset: List<String>,val moonPhase: List<String>,val tempMax: List<Float>, val tempMin: List<Float>,val iconDay: List<String>,val textDay: List<String>,val iconNight: List<String>,val textNight: List<String>,val wind360Day: List<String>,val windDirDay: List<String>,val windScaleDay: List<String>,val windSpeedDay: List<String>,val wind360Night: List<String>,val windDirNight: List<String>,val windScaleNight: List<String>,val windSpeedNight: List<String>,val humidity: List<Int>,val precip: List<Int>,val pressure: List<Int>,val vis: List<Int>,val cloud: List<Int>,val uvIndex: List<Int>)

    data class Refer(val sources: List<String>?,val license: List<String>?)

}