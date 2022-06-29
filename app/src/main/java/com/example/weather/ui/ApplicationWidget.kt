package com.example.weather.ui

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weather.CITY_KEY
import com.example.weather.R
import com.example.weather.WEATHER_KEY
import com.example.weather.workers.GetCityWorker
import com.example.weather.workers.GetWeatherWorker

//************************* not finished yet *************************

class ApplicationWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        val firstRequest = OneTimeWorkRequestBuilder<GetCityWorker>().build()
        val worker = WorkManager.getInstance(context)

        worker.enqueue(firstRequest)

        worker.getWorkInfoByIdLiveData(firstRequest.id).observe(ProcessLifecycleOwner.get()) { firstWorkInfo ->
            if (firstWorkInfo.outputData.getStringArray(CITY_KEY)?.isNotEmpty() == true) {
                val secondRequest = OneTimeWorkRequestBuilder<GetWeatherWorker>()
                    .setInputData(firstWorkInfo.outputData)
                    .build()
                worker.enqueue(secondRequest)

                worker.getWorkInfoByIdLiveData(secondRequest.id).observe(ProcessLifecycleOwner.get()) { secondWorkInfo ->
                    if (secondWorkInfo.outputData.getStringArray(WEATHER_KEY)?.isNotEmpty() == true) {
                        for (appWidgetId in appWidgetIds) {
                            updateAppWidget(context, appWidgetManager, appWidgetId, secondWorkInfo.outputData.getStringArray(WEATHER_KEY)!!)
                        }
                    }
                }
            }
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, weatherData: Array<String>) {
    val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    val views = RemoteViews(context.packageName, R.layout.application_widget).apply { setOnClickPendingIntent(R.id.widget_layout, pendingIntent) }
    views.setString(R.id.txt_current_temperature_widget, "", weatherData[0])
    appWidgetManager.updateAppWidget(appWidgetId, views)
}