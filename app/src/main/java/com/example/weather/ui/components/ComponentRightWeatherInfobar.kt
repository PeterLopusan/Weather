package com.example.weather.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.weather.R
import com.example.weather.databinding.ComponentRightWeatherInfobarBinding

class ComponentRightWeatherInfoBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttrs: Int = 0) : LinearLayout(context, attrs, defStyleAttrs) {

    private val binding: ComponentRightWeatherInfobarBinding by lazy {
        ComponentRightWeatherInfobarBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setValues(sunrise: String, sunset: String, wind: String, humidity: String) {
        binding.apply {
            txtSunrise.text = sunrise
            txtSunset.text = sunset
            txtWind.text = wind
            txtHumidity.text = humidity
        }
    }

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ComponentRightWeatherInfoBar)
        attributes.recycle()
    }
}