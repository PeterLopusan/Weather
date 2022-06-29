package com.example.weather.network

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class Weather(val location: Location, val current: Current, val forecast: Forecast) {

    data class Location(val country: String)

    data class Current(
        val humidity: String,
        val condition: Condition,
        @Json(name = "is_day") @SerializedName("is_day") val isDay: Int,
        @Json(name = "temp_c") @SerializedName("temp_c") val temperatureCelsius: String,
        @Json(name = "temp_f") @SerializedName("temp_f") val temperatureFahrenheit: String,
        @Json(name = "wind_kph") @SerializedName("wind_kph") val windKph: String,
        @Json(name = "wind_mph") @SerializedName("wind_mph") val windMph: String,
        @Json(name = "pressure_mb") @SerializedName("pressure_mb") val pressure: String,
        @Json(name = "feelslike_c") @SerializedName("feelslike_c") val feelsLikeCelsius: String,
        @Json(name = "feelslike_f") @SerializedName("feelslike_f") val feelsLikeFahrenheit: String
    ) {

        data class Condition(val text: String, val icon: String, val code: String)
    }

    data class Forecast(val forecastday: List<Forecastday>) {
        data class Forecastday(val date: String, val day: Day, val astro: Astro, val hour: List<Hour>) {
            data class Day(
                val condition: Condition,
                @Json(name = "maxtemp_c") @SerializedName("maxtemp_c") val maxTemperatureCelsius: String,
                @Json(name = "maxtemp_f") @SerializedName("maxtemp_f") val maxTemperatureFahrenheit: String,
                @Json(name = "mintemp_c") @SerializedName("mintemp_c") val minTemperatureCelsius: String,
                @Json(name = "mintemp_f") @SerializedName("mintemp_f") val minTemperatureFahrenheit: String,
                @Json(name = "maxwind_kph") @SerializedName("maxwind_kph") val maxWindKilometers: String,
                @Json(name = "maxwind_mph") @SerializedName("maxwind_mph") val maxWindMiles: String,
                @Json(name = "avghumidity") @SerializedName("avghumidity") val avgHumidity: String
            ) {
                data class Condition(val text: String, val icon: String, val code: String)
            }


            data class Astro(val sunrise: String, val sunset: String)

            data class Hour(
                val time: String,
                val condition: Condition,
                @Json(name = "temp_c") @SerializedName("temp_c") val temperatureCelsius: String,
                @Json(name = "temp_f") @SerializedName("temp_f") val temperatureFahrenheit: String,
                @Json(name = "time_epoch") @SerializedName("time_epoch") val timeEpoch: String,
            ) {
                data class Condition(val text: String, val icon: String, val code: String)
            }
        }
    }

}











