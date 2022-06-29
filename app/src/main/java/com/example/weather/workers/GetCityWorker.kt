package com.example.weather.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.weather.CITY_KEY
import com.example.weather.database.City
import com.example.weather.database.CityRoomDatabase
import com.example.weather.utils.SharedPreferencesUtils

class GetCityWorker(private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        return try {
            val city = findCity(CityRoomDatabase.getDatabase(applicationContext).cityDao().getAllRecordForWorker())
            val output = arrayOf(city.latitude, city.longitude, city.name)
            val outputData = workDataOf(CITY_KEY to output)
            return Result.success(outputData)

        } catch (throwable: Throwable) {
            Result.failure()
        }
    }

    private fun findCity(list: List<City>): City {
        if (SharedPreferencesUtils.getFavouriteCityId(context) != -1) {
            if (list.isNotEmpty()) {
                for (city in list) {
                    if (city.id == SharedPreferencesUtils.getFavouriteCityId(context)) {
                        return City(name = city.name, country = city.country, latitude = city.latitude, longitude = city.longitude)
                    }
                }
            }
        }

        val city = SharedPreferencesUtils.getLastCity(context)
        val country = SharedPreferencesUtils.getLastCountry(context)
        val latitude = SharedPreferencesUtils.getLastLatitude(context)
        val longitude = SharedPreferencesUtils.getLastLongitude(context)
        return City(name = city, country = country, latitude = latitude, longitude = longitude)
    }
}