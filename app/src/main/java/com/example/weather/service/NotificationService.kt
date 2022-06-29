package com.example.weather.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.NotificationTarget
import com.example.weather.CITY_KEY
import com.example.weather.R
import com.example.weather.WEATHER_KEY
import com.example.weather.ui.MainActivity
import com.example.weather.utils.DateUtils
import com.example.weather.utils.SharedPreferencesUtils
import com.example.weather.utils.getBackground
import com.example.weather.workers.GetCityWorker
import com.example.weather.workers.GetWeatherWorker
import kotlinx.coroutines.*


private const val CHANNEL_ID = "Channel ID"

class NotificationService : LifecycleService() {

    @DelicateCoroutinesApi
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRunning = true
        createNotificationChannel()
        startForeground(startId, NotificationCompat.Builder(this, CHANNEL_ID).setPriority(0).build())

        val firstRequest = OneTimeWorkRequestBuilder<GetCityWorker>().build()
        val worker = WorkManager.getInstance(this)

        worker.enqueue(firstRequest)
        worker.getWorkInfoByIdLiveData(firstRequest.id).observe(this) { firstWorkInfo ->

            if (firstWorkInfo.outputData.getStringArray(CITY_KEY)?.isNotEmpty() == true) {
                val secondRequest = OneTimeWorkRequestBuilder<GetWeatherWorker>().setInputData(firstWorkInfo.outputData).build()
                worker.enqueue(secondRequest)
                worker.getWorkInfoByIdLiveData(secondRequest.id).observe(this) { secondWorkInfo ->

                    if (secondWorkInfo.outputData.getStringArray(WEATHER_KEY)?.isNotEmpty() == true) {
                        createNotification(startId, secondWorkInfo.outputData.getStringArray(WEATHER_KEY)!!)
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val name = this.getString(R.string.permanent_notification)
            val notificationDescription = this.getString(R.string.permanent_notification)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = notificationDescription
                vibrationPattern = LongArray(0)
                enableVibration(true)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @DelicateCoroutinesApi
    private fun createNotification(id: Int, weather: Array<String>) {
        val extendedNotificationLayout: RemoteViews
        val notificationLayout: RemoteViews
        val isDay = weather[14].toInt()

        if (isDay == 1) {
            extendedNotificationLayout = RemoteViews(packageName, R.layout.notification_extended_layout_day)
            notificationLayout = RemoteViews(packageName, R.layout.notification_layout_day)
        } else {
            extendedNotificationLayout = RemoteViews(packageName, R.layout.notification_extended_layout_night)
            notificationLayout = RemoteViews(packageName, R.layout.notification_layout_night)
        }


        val resultIntent = Intent(this, MainActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }


        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCustomContentView(notificationLayout)
            .setContentIntent(resultPendingIntent)
            .setCustomBigContentView(extendedNotificationLayout)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())

        setNotificationLayout(notificationLayout, weather)
        setExtendedNotificationLayout(extendedNotificationLayout, weather, builder.build(), id)

        with(NotificationManagerCompat.from(this)) {
            notify(id, builder.build())
        }
    }


    private fun setNotificationLayout(layout: RemoteViews, weather: Array<String>) {
        val city = weather[0]
        val temperature = weather[1].toDouble().toInt()
        val description = weather[2]
        val code = weather[4]
        val isDay = weather[14].toInt()

        layout.apply {
            setTextViewText(R.id.txt_city_notification_layout, city)
            setTextViewText(R.id.txt_weather_description_notification_layout, description)

            if (SharedPreferencesUtils.getTemperatureUnit(this@NotificationService) == SharedPreferencesUtils.defaultTemperatureUnit) {
                setTextViewText(R.id.txt_temperature_notification_layout, this@NotificationService.getString(R.string.temperature_value_celsius, temperature))
            } else {
                setTextViewText(R.id.txt_temperature_notification_layout, this@NotificationService.getString(R.string.temperature_value_fahrenheit, temperature))
            }
            setInt(R.id.notification_layout, "setBackgroundResource", getBackground(code, isDay))
        }
    }

    @DelicateCoroutinesApi
    private fun setExtendedNotificationLayout(layout: RemoteViews, weather: Array<String>, notification: Notification, notificationId: Int) {
        val city = weather[0]
        val currentTemperature = weather[1].toDouble().toInt()
        val weatherDescription = weather[2]
        val currentWeatherIcon = weather[3]
        val currentWeatherCode = weather[4]
        val todayMin = weather[5].toDouble().toInt()
        val todayMax = weather[6].toDouble().toInt()
        val todayIcon = weather[7]
        val tomorrowMin = weather[8].toDouble().toInt()
        val tomorrowMax = weather[9].toDouble().toInt()
        val tomorrowIcon = weather[10]
        val afterTomorrowMin = weather[11].toDouble().toInt()
        val afterTomorrowMax = weather[12].toDouble().toInt()
        val afterTomorrowIcon = weather[13]
        val isDay = weather[14].toInt()

        layout.apply {
            if (SharedPreferencesUtils.getTemperatureUnit(this@NotificationService) == SharedPreferencesUtils.defaultTemperatureUnit) {
                setTextViewText(R.id.txt_temperature_extended_layout, this@NotificationService.getString(R.string.temperature_value_celsius, currentTemperature))
                setTextViewText(R.id.txt_today_temperature_extended_layout, this@NotificationService.getString(R.string.min_max_temperature_value_celsius, todayMin, todayMax))
                setTextViewText(R.id.txt_tomorrow_temperature_extended_layout, this@NotificationService.getString(R.string.min_max_temperature_value_celsius, tomorrowMin, tomorrowMax))
                setTextViewText(
                    R.id.txt_day_after_tomorrow_temperature_extended_layout,
                    this@NotificationService.getString(R.string.min_max_temperature_value_celsius, afterTomorrowMin, afterTomorrowMax)
                )
            } else {
                setTextViewText(R.id.txt_temperature_extended_layout, this@NotificationService.getString(R.string.temperature_value_fahrenheit, currentTemperature))
                setTextViewText(R.id.txt_today_temperature_extended_layout, this@NotificationService.getString(R.string.min_max_temperature_value_fahrenheit, todayMin, todayMax))
                setTextViewText(R.id.txt_tomorrow_temperature_extended_layout, this@NotificationService.getString(R.string.min_max_temperature_value_fahrenheit, tomorrowMin, tomorrowMax))
                setTextViewText(
                    R.id.txt_day_after_tomorrow_temperature_extended_layout,
                    this@NotificationService.getString(R.string.min_max_temperature_value_fahrenheit, afterTomorrowMin, afterTomorrowMax)
                )
            }

            setTextViewText(R.id.txt_city_extended_layout, city)
            setTextViewText(R.id.txt_weather_description_extended_layout, weatherDescription)
            setTextViewText(R.id.txt_day_after_tomorrow_today_extended_layout, DateUtils.getAfterTomorrowDay())
            setImage(layout, currentWeatherIcon, R.id.img_current_weather__extended_layout, notification, notificationId)
            setImage(layout, todayIcon, R.id.img_today_weather_extended_layout, notification, notificationId)
            setImage(layout, tomorrowIcon, R.id.img_tomorrow_weather_extended_layout, notification, notificationId)
            setImage(layout, afterTomorrowIcon, R.id.img_day_after_tomorrow_weather_extended_layout, notification, notificationId)
            setTextViewText(R.id.txt_refresh_time_extended_layout, this@NotificationService.getString(R.string.last_update, DateUtils.getCurrentTime()))
            setInt(R.id.extended_notification_layout, "setBackgroundResource", getBackground(currentWeatherCode, isDay))

            val intent = Intent(this@NotificationService, NotificationService::class.java)
            val pendingIntent = PendingIntent.getService(this@NotificationService, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            setOnClickPendingIntent(R.id.btn_refresh_notification, pendingIntent)
        }
    }


    @DelicateCoroutinesApi
    private fun setImage(layout: RemoteViews, url: String, viewId: Int, notification: Notification, notificationId: Int) {
        GlobalScope.launch {
            try {
                val notificationTarget = NotificationTarget(this@NotificationService, viewId, layout, notification, notificationId)
                val bitmap: Bitmap = Glide.with(this@NotificationService)
                    .asBitmap()
                    .load("https:${url}")
                    .submit()
                    .get()

                Glide
                    .with(this@NotificationService.applicationContext)
                    .asBitmap()
                    .load(bitmap)
                    .into(notificationTarget)

            } catch (e: Exception) {
                Log.d("TAG", "$e")
            }
        }
    }

    override fun onDestroy() {
        isRunning = false
        super.onDestroy()
    }

    companion object {
        private var isRunning = false

        fun getIsRunning(): Boolean {
            return isRunning
        }
    }
}

class RefreshButtonReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, p1: Intent?) {
        NotificationServiceManager.resetService(context)
    }
}