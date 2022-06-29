package com.example.weather.service

import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weather.workers.ServiceWorker
import java.util.concurrent.TimeUnit


const val WORK_NAME = "workName"

class NotificationServiceManager {

    companion object {
        fun startService(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            val request = PeriodicWorkRequestBuilder<ServiceWorker>(15, TimeUnit.MINUTES).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.REPLACE, request)
        }

        fun stopService(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            val intent = Intent(context, NotificationService::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.stopService(intent)
        }

        fun resetService(context: Context) {
            if (NotificationService.getIsRunning()) {
                stopService(context)
                startService(context)
            }
        }
    }
}