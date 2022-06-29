package com.example.weather.ui.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.HourlyWeatherAdapterBinding
import com.example.weather.network.Weather
import com.example.weather.utils.DateUtils
import com.example.weather.utils.SharedPreferencesUtils
import com.example.weather.utils.setImage

class HourlyWeatherAdapter(private val context: Context) : ListAdapter<Weather.Forecast.Forecastday.Hour, HourlyWeatherAdapter.HourlyWeatherViewHolder>(DiffCallbackWeather) {

    class HourlyWeatherViewHolder(private val context: Context, private val binding: HourlyWeatherAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setHourlyWeatherValues(hourlyWeather: Weather.Forecast.Forecastday.Hour) {
            binding.apply {
                txtTime.text = DateUtils.formatDateForHourlyAdapter(hourlyWeather.time)
                setImage(hourlyWeather.condition.icon, imgHourlyWeather)

                if (SharedPreferencesUtils.getTemperatureUnit(context) == SharedPreferencesUtils.defaultTemperatureUnit) {
                    txtHourlyTemperature.text = context.getString(R.string.temperature_value_celsius, hourlyWeather.temperatureCelsius.toDouble().toInt())
                } else {
                    txtHourlyTemperature.text = context.getString(R.string.temperature_value_fahrenheit, hourlyWeather.temperatureFahrenheit.toDouble().toInt())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        return HourlyWeatherViewHolder(context, HourlyWeatherAdapterBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        val weatherHour = getItem(position)
        holder.setHourlyWeatherValues(weatherHour)
    }

    companion object DiffCallbackWeather : DiffUtil.ItemCallback<Weather.Forecast.Forecastday.Hour>() {
        override fun areItemsTheSame(oldItem: Weather.Forecast.Forecastday.Hour, newItem: Weather.Forecast.Forecastday.Hour): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: Weather.Forecast.Forecastday.Hour, newItem: Weather.Forecast.Forecastday.Hour): Boolean {
            return oldItem == newItem
        }
    }
}
