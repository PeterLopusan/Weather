package com.example.weather.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import com.example.weather.R
import com.example.weather.databinding.ComponentCurrentWeatherViewBinding
import com.example.weather.databinding.ComponentDescriptionIconBinding
import com.example.weather.network.Weather
import com.example.weather.utils.*

class ComponentCurrentWeatherView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttrs: Int = 0) : LinearLayout(context, attrs, defStyleAttrs) {

    fun setValues(weather: Weather, city: String, country: String) {
        this.removeAllViews()

        val weatherCode = weather.current.condition.code
        val binding: ComponentCurrentWeatherViewBinding by lazy {
            ComponentCurrentWeatherViewBinding.inflate(LayoutInflater.from(getStyle(weatherCode, weather.current.isDay, context)), this, true)
        }

        binding.apply {

            val sunrise = context.getString(R.string.sunrise, DateUtils.formatTimeTo24Format(weather.forecast.forecastday[0].astro.sunrise))
            val sunset = context.getString(R.string.sunset, DateUtils.formatTimeTo24Format(weather.forecast.forecastday[0].astro.sunset))
            val humidity = context.getString(R.string.humidity, weather.current.humidity)
            val wind: String = if (SharedPreferencesUtils.getSpeedUnit(context) == SharedPreferencesUtils.defaultSpeedUnit) {
                context.getString(R.string.wind_kilometers, weather.current.windKph.toDouble().toInt())
            } else {
                context.getString(R.string.wind_miles, weather.current.windMph.toDouble().toInt())
            }

            componentRightWeatherInfobar.setValues(sunrise, sunset, wind, humidity)
            txtCity.text = city
            txtCountry.text = country
            layoutComponentCurrentWeatherView.background = AppCompatResources.getDrawable(context, getBackground(weatherCode, weather.current.isDay))

            if (SharedPreferencesUtils.getTemperatureUnit(context) == SharedPreferencesUtils.defaultTemperatureUnit) {
                txtTemperature.text = context.getString(R.string.temperature_value_celsius, weather.current.temperatureCelsius.toDouble().toInt())
                txtFeelsLike.text = context.getString(R.string.temperature_value_celsius, weather.current.feelsLikeCelsius.toDouble().toInt())
            } else {
                txtTemperature.text = context.getString(R.string.temperature_value_fahrenheit, weather.current.temperatureFahrenheit.toDouble().toInt())
                txtFeelsLike.text = context.getString(R.string.temperature_value_fahrenheit, weather.current.feelsLikeFahrenheit.toDouble().toInt())
            }

            if (weather.current.isDay == 1) {
                componentHourlyWeatherRecyclerView.setRecycler(getWeatherDataForAdapter(weather), false)
            } else {
                componentHourlyWeatherRecyclerView.setRecycler(getWeatherDataForAdapter(weather), true)
            }

            layoutDescriptionIcon.apply {
                removeAllViews()
                ComponentDescriptionIconBinding.inflate(LayoutInflater.from(context), this, true).apply {
                    txtWeatherDescription.text = weather.current.condition.text
                    setImage(weather.current.condition.icon, imgWeatherIcon)
                }
            }
        }
    }

    private fun getWeatherDataForAdapter(weather: Weather): List<Weather.Forecast.Forecastday.Hour> {
        return weather.forecast.forecastday[0].hour.filter { System.currentTimeMillis() < (it.timeEpoch.toLong() * 1000) }
    }

}