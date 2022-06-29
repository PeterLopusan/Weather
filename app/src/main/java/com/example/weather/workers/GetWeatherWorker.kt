package com.example.weather.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.weather.*
import com.example.weather.network.Weather
import com.example.weather.network.WeatherApi
import com.example.weather.utils.SharedPreferencesUtils

class GetWeatherWorker(val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        val inputData = inputData.getStringArray(CITY_KEY)
        val latitude = inputData?.get(0)
        val longitude = inputData?.get(1)
        val cityName = inputData?.get(2)
        val weather: Weather

        return try {
            weather = WeatherApi.retrofitService.getWeather("$latitude,$longitude", DAYS, AQI, ALERTS, LANG_SK)

            val output: Array<String>
            val currentTemperature: String
            val currentWeatherDescription = weather.current.condition.text
            val currentWeatherIcon = weather.current.condition.icon
            val currentWeatherCode = weather.current.condition.code

            val todayMin: String
            val todayMax: String
            val todayIcon = weather.forecast.forecastday[0].day.condition.icon

            val tomorrowMin: String
            val tomorrowMax: String
            val tomorrowIcon = weather.forecast.forecastday[1].day.condition.icon

            val afterTomorrowMin: String
            val afterTomorrowMax: String
            val afterTomorrowIcon = weather.forecast.forecastday[2].day.condition.icon

            val isDay = weather.current.isDay.toString()


            if (SharedPreferencesUtils.getTemperatureUnit(context) == SharedPreferencesUtils.defaultTemperatureUnit) {
                currentTemperature = weather.current.temperatureCelsius

                todayMin = weather.forecast.forecastday[0].day.minTemperatureCelsius
                todayMax = weather.forecast.forecastday[0].day.maxTemperatureCelsius

                tomorrowMin = weather.forecast.forecastday[1].day.minTemperatureCelsius
                tomorrowMax = weather.forecast.forecastday[1].day.maxTemperatureCelsius

                afterTomorrowMin = weather.forecast.forecastday[2].day.minTemperatureCelsius
                afterTomorrowMax = weather.forecast.forecastday[2].day.maxTemperatureCelsius

            } else {
                currentTemperature = weather.current.temperatureFahrenheit

                todayMin = weather.forecast.forecastday[0].day.minTemperatureFahrenheit
                todayMax = weather.forecast.forecastday[0].day.maxTemperatureFahrenheit

                tomorrowMin = weather.forecast.forecastday[1].day.minTemperatureFahrenheit
                tomorrowMax = weather.forecast.forecastday[1].day.maxTemperatureFahrenheit

                afterTomorrowMin = weather.forecast.forecastday[2].day.minTemperatureFahrenheit
                afterTomorrowMax = weather.forecast.forecastday[2].day.maxTemperatureFahrenheit
            }

            output = arrayOf(
                cityName!!,
                currentTemperature,
                currentWeatherDescription,
                currentWeatherIcon,
                currentWeatherCode,
                todayMin,
                todayMax,
                todayIcon,
                tomorrowMin,
                tomorrowMax,
                tomorrowIcon,
                afterTomorrowMin,
                afterTomorrowMax,
                afterTomorrowIcon,
                isDay
            )

            val outputData = workDataOf(WEATHER_KEY to output)
            return Result.success(outputData)

        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}


