package com.example.weather.utils

import android.content.Context
import com.example.weather.R
import java.util.*


object SharedPreferencesUtils {

    private const val sharedPrefName = ""
    private const val defaultCity = "Bratislava"
    private const val defaultCountry = "Slovakia"
    private const val defaultLatitude = "49.227528"
    private const val defaultLongitude = "18.735385"
    private const val defaultBackupLocationId = -2

    const val defaultTemperatureUnit = "Celsius"
    const val defaultSpeedUnit = "Kilometers"
    const val defaultMapType = "Normal"

    const val changedTemperatureUnit = "Fahrenheit "
    const val changedSpeedUnit = "Miles"
    const val changedMapType = "Satellite"

    fun getFavouriteCityId(context: Context): Int {
        val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        return sharedPref.getInt(context.getString(R.string.shared_pref_id), -1)
    }

    fun setFavouriteCityId(context: Context, id: Int) {
        val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        sharedPref.edit().putInt(context.getString(R.string.shared_pref_id), id).apply()
    }

    fun setLastLatitude(context: Context, latitude: String) {
        val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        sharedPref.edit().putString(context.getString(R.string.shared_pref_latitude), latitude).apply()
    }

    fun getLastLatitude(context: Context): String {
        val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        return sharedPref.getString(context.getString(R.string.shared_pref_latitude), defaultLatitude)!!
    }

    fun setLastLongitude(context: Context, longitude: String) {
        val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        sharedPref.edit().putString(context.getString(R.string.shared_pref_longitude), longitude).apply()
    }

    fun getLastLongitude(context: Context): String {
        val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        return sharedPref.getString(context.getString(R.string.shared_pref_longitude), defaultLongitude)!!
    }

    fun setLastCity(context: Context, city: String) {
        val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        sharedPref.edit().putString(context.getString(R.string.shared_pref_city), city).apply()
    }

    fun getLastCity(context: Context): String {
        val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        return sharedPref.getString(context.getString(R.string.shared_pref_city), defaultCity)!!
    }

    fun setLastCountry(context: Context, country: String) {
        val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        sharedPref.edit().putString(context.getString(R.string.shared_pref_country), country).apply()
    }

    fun getLastCountry(context: Context): String {
        val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        return sharedPref.getString(context.getString(R.string.shared_pref_country), defaultCountry)!!
    }

    fun setTemperatureUnit(context: Context, temperatureUnit: String) {
        val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        sharedPref.edit().putString(context.getString(R.string.shared_pref_temperature_unit), temperatureUnit).apply()
    }

    fun getTemperatureUnit(context: Context): String {
        val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        return sharedPref.getString(context.getString(R.string.shared_pref_temperature_unit), defaultTemperatureUnit)!!
    }

    fun setSpeedUnit(context: Context, speedUnit: String) {
        val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        sharedPref.edit().putString(context.getString(R.string.shared_pref_speed_unit), speedUnit).apply()
    }

    fun getSpeedUnit(context: Context): String {
        val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        return sharedPref.getString(context.getString(R.string.shared_pref_speed_unit), defaultSpeedUnit)!!
    }

    fun setMapType(context: Context, mapType: String) {
        val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        sharedPref.edit().putString(context.getString(R.string.shared_pref_map_type), mapType).apply()
    }

    fun getMapType(context: Context): String {
        val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        return sharedPref.getString(context.getString(R.string.shared_pref_map_type), defaultMapType)!!
    }

    fun setBackupLocationId(context: Context, locationId: Int) {
        val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        sharedPref.edit().putInt(context.getString(R.string.shared_pref_backup_location_id), locationId).apply()
    }

    fun getBackupLocationId(context: Context): Int {
        val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        return sharedPref.getInt(context.getString(R.string.shared_pref_backup_location_id), defaultBackupLocationId)
    }

    fun setLocalization(context: Context, language: String) {
        val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        sharedPref.edit().putString(context.getString(R.string.shared_pref_language), language).apply()
    }

    fun getLocalization(context: Context): String {
        val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        return sharedPref.getString(context.getString(R.string.shared_pref_language), Locale.getDefault().country)!!
    }
}