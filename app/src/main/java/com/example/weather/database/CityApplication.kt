package com.example.weather.database

import android.app.Application

class CityApplication: Application() {
    val database: CityRoomDatabase by lazy { CityRoomDatabase.getDatabase(this) }

}