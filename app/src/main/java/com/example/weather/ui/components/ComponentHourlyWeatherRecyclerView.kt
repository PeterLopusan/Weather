package com.example.weather.ui.components

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.weather.R
import com.example.weather.databinding.ComponentHourlyWeatherRecyclerViewBinding
import com.example.weather.network.Weather
import com.example.weather.ui.adapters.HourlyWeatherAdapter

class ComponentHourlyWeatherRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttrs: Int = 0) : LinearLayout(context, attrs, defStyleAttrs) {

    private val binding: ComponentHourlyWeatherRecyclerViewBinding by lazy {
        ComponentHourlyWeatherRecyclerViewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setRecycler(recyclerData: List<Weather.Forecast.Forecastday.Hour>, setWhite: Boolean) {
        binding.recyclerViewHourlyWeather.apply {
            val dividerItemDecoration = DividerItemDecoration(context, HORIZONTAL)
            val recyclerAdapter = HourlyWeatherAdapter(context)

            if (setWhite) {
                dividerItemDecoration.setDrawable(ColorDrawable(ContextCompat.getColor(context, R.color.white)))
            } else {
                dividerItemDecoration.setDrawable(ColorDrawable(ContextCompat.getColor(context, R.color.black)))
            }

            addItemDecoration(dividerItemDecoration)
            adapter = recyclerAdapter
            recyclerAdapter.submitList(recyclerData)
        }
    }
}