package com.example.weather.workers

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weather.service.NotificationService

class ServiceWorker(private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        if (checkInternetConnection()) {
            val intent = Intent(context, NotificationService::class.java)

            context.stopService(intent)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        return Result.success()
    }

    private fun checkInternetConnection(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        if (capabilities != null) {
            return true
        }
        return false
    }
}