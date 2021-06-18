package com.sunnyweather.android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.ui.weather.WeatherActivity
import kotlin.concurrent.thread

class MyService : Service() {

    private var placeName = ""
    private var temp = ""
    private var text = ""
    private var humidity = ""

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MyService", "onCreate executed")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            placeName = intent.getStringExtra("placeName").toString()
            temp = intent.getStringExtra("temp").toString()
            text = intent.getStringExtra("text").toString()
            humidity = intent.getStringExtra("humidity").toString()
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("my_service", "前台Service通知", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, "my_service")
            .setContentTitle("${placeName}")
            .setContentText("温度：${temp}℃   天气状态：${text}   湿度：${humidity}")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .build()
        startForeground(1, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyService", "onDestroy executed")
    }

    public fun getWeather(weather: Weather){

    }
}