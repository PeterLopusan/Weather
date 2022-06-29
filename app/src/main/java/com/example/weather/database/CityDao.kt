package com.example.weather.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(city: City)

    @Delete
    fun delete(city: City)

    @Query("SELECT * from city ORDER BY name ASC")
    fun getAllRecord(): Flow<List<City>>

    @Query("SELECT * from city ORDER BY id")
    fun getAllRecordForWorker(): List<City>
}