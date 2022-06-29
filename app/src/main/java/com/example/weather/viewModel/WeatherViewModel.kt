package com.example.weather.viewModel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import com.example.weather.*
import com.example.weather.database.City
import com.example.weather.database.CityDao
import com.example.weather.network.Weather
import com.example.weather.network.WeatherApi
import com.example.weather.utils.SharedPreferencesUtils
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private const val FILE_NAME = "backup.txt"
private const val DIRECTORY_NAME = "BACKUP.txt"
private const val NAME = "Bratislava"
private const val COUNTRY = "Slovensko"


class WeatherViewModel(private val cityDao: CityDao, application: Application) : AndroidViewModel(application) {
    private val _weather = MutableLiveData<Weather>()
    val weather: LiveData<Weather> get() = _weather

    var listOfCities: LiveData<List<City>> = cityDao.getAllRecord().asLiveData()
    var currentCity = City(name = NAME, country = COUNTRY, latitude = LATITUDE, longitude = LONGITUDE)

    val errorMessage = MutableLiveData<String>()

    fun insertCity(city: City) {
        viewModelScope.launch {
            cityDao.insert(city)
        }
    }

    fun setCurrentCity(context: Context) {
        if (SharedPreferencesUtils.getFavouriteCityId(context) == -1) {
            val city = SharedPreferencesUtils.getLastCity(context)
            val country = SharedPreferencesUtils.getLastCountry(context)
            val latitude = SharedPreferencesUtils.getLastLatitude(context)
            val longitude = SharedPreferencesUtils.getLastLongitude(context)
            currentCity = City(name = city, country = country, latitude = latitude, longitude = longitude)
        } else {
            if (listOfCities.value?.isNotEmpty() == true) {
                for (city in listOfCities.value!!) {
                    if (city.id == SharedPreferencesUtils.getFavouriteCityId(context)) {
                        currentCity = City(name = city.name, country = city.country, latitude = city.latitude, longitude = city.longitude)
                    }
                }
            }
        }
        getWeatherData()
    }

    private fun getWeatherData() {
        val context = getApplication<Application>().applicationContext
        viewModelScope.launch {
            try {
                val language = context.getString(R.string.language_for_url)
                _weather.value = WeatherApi.retrofitService.getWeather("${currentCity.latitude},${currentCity.longitude}", DAYS, AQI, ALERTS, language)
                WeatherApi.retrofitService.getRawJsonData("${currentCity.latitude},${currentCity.longitude}", DAYS, AQI, ALERTS, language).enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(context, context.getText(R.string.backup_failed), Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        val stringResponse = response.body()?.string()
                        if (stringResponse != null) {
                            makeWeatherBackup(stringResponse)
                        } else {
                            Toast.makeText(context, context.getText(R.string.backup_failed), Toast.LENGTH_LONG).show()
                        }

                    }
                })
            } catch (e: Exception) {
                Toast.makeText(context, context.getText(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
                readFromWeatherBackup()
            }
        }
    }


    private fun makeWeatherBackup(json: String) {
        val context = getApplication<Application>().applicationContext
        val file = makeFile(context)

        file.writeText(json)
        SharedPreferencesUtils.setBackupLocationId(context, SharedPreferencesUtils.getFavouriteCityId(context))
    }

    private fun makeFile(context: Context): File {
        val path = context.filesDir
        val letDirectory = File(path, DIRECTORY_NAME)
        letDirectory.mkdirs()
        return File(letDirectory, FILE_NAME)
    }

    private fun readFromWeatherBackup() {
        val context = getApplication<Application>().applicationContext
        if (SharedPreferencesUtils.getFavouriteCityId(context) == SharedPreferencesUtils.getBackupLocationId(context)) {
            try {
                val stringFromFile: String = makeFile(context).readText()
                _weather.value = Gson().fromJson(stringFromFile, Weather::class.java)
            } catch (e: Exception) {
                errorMessage.value = e.message.toString()
            }
        } else {
            errorMessage.value = context.getString(R.string.no_backup_for_this_id)
        }
    }
}


//****************************************************************************************

class WeatherViewModelFactory(private val cityDao: CityDao, private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(cityDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

