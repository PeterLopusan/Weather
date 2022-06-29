package com.example.weather.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

private const val BASE_URL = "http://api.weatherapi.com"
private const val EXTENDED_URL = "/v1/forecast.json?key=19e79c776d8b4f6fa7d110027211011"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()


interface WeatherApiService {

    @GET(EXTENDED_URL)
    suspend fun getWeather(
        @Query("q") latitudeLongitude: String,
        @Query("days") days: String,
        @Query("aqi") aqi: String,
        @Query("alerts") alerts: String,
        @Query("lang") lang: String,
    ): Weather


    @POST(EXTENDED_URL)
    fun getRawJsonData(
        @Query("q") latitudeLongitude: String,
        @Query("days") days: String,
        @Query("aqi") aqi: String,
        @Query("alerts") alerts: String,
        @Query("lang") lang: String,
    ): Call<ResponseBody>

}


object WeatherApi {
    val retrofitService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}