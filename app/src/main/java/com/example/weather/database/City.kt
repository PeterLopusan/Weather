package com.example.weather.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class City(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "country")
    var country: String,
    @ColumnInfo(name = "latitude")
    var latitude: String,
    @ColumnInfo(name = "longitude")
    var longitude: String
)