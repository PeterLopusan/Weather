package com.example.weather.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.ComponentDescriptionIconBinding
import com.example.weather.databinding.ComponentForecastViewBinding
import com.example.weather.network.Weather
import com.example.weather.utils.*

class ForecastAdapter(private val context: Context) : ListAdapter<Weather.Forecast.Forecastday, ForecastAdapter.ForecastViewHolder>(DiffCallbackForecast) {

    inner class ForecastViewHolder(private val viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup) {

        fun setForecastLayout(weather: Weather.Forecast.Forecastday) {
            viewGroup.removeAllViews()
            val weatherCode = weather.day.condition.code
            val binding = ComponentForecastViewBinding.inflate(LayoutInflater.from(getStyle(weatherCode, 1, context)), viewGroup, true)

            binding.apply {
                layoutComponentForecastView.background = AppCompatResources.getDrawable(context, getBackground(weatherCode, 1))
                componentHourlyWeatherRecyclerView.setRecycler(weather.hour, false)
                txtDate.text = DateUtils.formatDateForForecast(weather.date)

                if (SharedPreferencesUtils.getTemperatureUnit(context) == SharedPreferencesUtils.defaultTemperatureUnit) {
                    txtMaxTemperature.text = context.getString(R.string.temperature_value_celsius, weather.day.maxTemperatureCelsius.toDouble().toInt())
                    txtMinTemperature.text = context.getString(R.string.temperature_value_celsius, weather.day.minTemperatureCelsius.toDouble().toInt())
                } else {
                    txtMaxTemperature.text = context.getString(R.string.temperature_value_fahrenheit, weather.day.maxTemperatureFahrenheit.toDouble().toInt())
                    txtMinTemperature.text = context.getString(R.string.temperature_value_fahrenheit, weather.day.minTemperatureFahrenheit.toDouble().toInt())
                }

                val sunrise = context.getString(R.string.sunrise, DateUtils.formatTimeTo24Format(weather.astro.sunrise))
                val sunset = context.getString(R.string.sunset, DateUtils.formatTimeTo24Format(weather.astro.sunset))
                val humidity = context.getString(R.string.humidity, weather.day.avgHumidity)
                val wind: String = if (SharedPreferencesUtils.getSpeedUnit(context) == SharedPreferencesUtils.defaultSpeedUnit) {
                    context.getString(R.string.wind_kilometers, weather.day.maxWindKilometers.toDouble().toInt())
                } else {
                    context.getString(R.string.wind_miles, weather.day.maxWindMiles.toDouble().toInt())
                }

                componentRightWeatherInfobar.setValues(sunrise, sunset, humidity, wind)

                layoutDescriptionIcon.apply {
                    removeAllViews()
                    ComponentDescriptionIconBinding.inflate(LayoutInflater.from(context), this, true).apply {
                        txtWeatherDescription.text = weather.day.condition.text
                        setImage(weather.day.condition.icon, imgWeatherIcon)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        return ForecastViewHolder(LinearLayout(context))
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val weatherHour = getItem(position)
        holder.setForecastLayout(weatherHour)
    }

    companion object DiffCallbackForecast : DiffUtil.ItemCallback<Weather.Forecast.Forecastday>() {
        override fun areItemsTheSame(oldItem: Weather.Forecast.Forecastday, newItem: Weather.Forecast.Forecastday): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: Weather.Forecast.Forecastday, newItem: Weather.Forecast.Forecastday): Boolean {
            return oldItem == newItem
        }
    }
}
