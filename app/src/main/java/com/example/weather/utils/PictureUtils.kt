package com.example.weather.utils

import android.content.Context
import android.view.ContextThemeWrapper
import android.widget.ImageView
import com.example.weather.R
import com.example.weather.WeatherType
import com.squareup.picasso.Picasso


fun setImage(url: String?, imageView: ImageView) {
    val newUrl = "https:$url"
    Picasso.get().load(newUrl).into(imageView)
}

fun getStyle(weatherCode: String, isDay: Int, context: Context): ContextThemeWrapper {
    val contextThemeWrapper: ContextThemeWrapper

    when (WeatherType.getType(weatherCode)) {
        WeatherType.SUNNY_CLEAR -> {
            contextThemeWrapper = if (isDay == 1) {
                ContextThemeWrapper(context, R.style.WeatherTheme_BlackColorText)
            } else {
                ContextThemeWrapper(context, R.style.WeatherTheme_WhiteColorText)
            }
        }

        WeatherType.RAIN -> {
            contextThemeWrapper = if (isDay == 1) {
                ContextThemeWrapper(context, R.style.WeatherTheme_BlackColorText)
            } else {
                ContextThemeWrapper(context, R.style.WeatherTheme_WhiteColorText)
            }
        }

        WeatherType.CLOUDY -> {
            contextThemeWrapper = ContextThemeWrapper(context, R.style.WeatherTheme_BlackColorText)
        }

        WeatherType.CLEAR -> {
            contextThemeWrapper = ContextThemeWrapper(context, R.style.WeatherTheme_WhiteColorText)
        }

        WeatherType.PARTLY_CLOUD -> {
            contextThemeWrapper = if (isDay == 1) {
                ContextThemeWrapper(context, R.style.WeatherTheme_BlackColorText)
            } else {
                ContextThemeWrapper(context, R.style.WeatherTheme_WhiteColorText)
            }
        }

        WeatherType.FREEZING -> {
            contextThemeWrapper = ContextThemeWrapper(context, R.style.WeatherTheme_BlackColorText)
        }

        WeatherType.SNOW -> {
            contextThemeWrapper = if (isDay == 1) {
                ContextThemeWrapper(context, R.style.WeatherTheme_BlackColorText)
            } else {
                ContextThemeWrapper(context, R.style.WeatherTheme_WhiteColorText)
            }
        }

        WeatherType.MIST -> {
            contextThemeWrapper = if (isDay == 1) {
                ContextThemeWrapper(context, R.style.WeatherTheme_BlackColorText)
            } else {
                ContextThemeWrapper(context, R.style.WeatherTheme_WhiteColorText)
            }
        }

        WeatherType.HAIL -> {
            contextThemeWrapper = ContextThemeWrapper(context, R.style.WeatherTheme_BlackColorText)
        }
    }
    return contextThemeWrapper
}

fun getBackground(weatherCode: String, isDay: Int): Int {
    when (WeatherType.getType(weatherCode)) {
        WeatherType.SUNNY_CLEAR -> {
            return if (isDay == 1) {
                R.drawable.sunny
            } else {
                R.drawable.clear
            }
        }

        WeatherType.RAIN -> {
            return if (isDay == 1) {
                R.drawable.rain
            } else {
                R.drawable.rain
            }
        }

        WeatherType.CLOUDY -> {
            return R.drawable.cloudy
        }

        WeatherType.PARTLY_CLOUD -> {
            return if (isDay == 1) {
                R.drawable.partly_cloudy
            } else {
                R.drawable.partly_cloudy_night
            }
        }

        WeatherType.FREEZING -> {
            R.drawable.freezing
        }

        WeatherType.SNOW -> {
            return if (isDay == 1) {
                R.drawable.snow
            } else {
                R.drawable.snow_night
            }
        }

        WeatherType.MIST -> {
            return if (isDay == 1) {
                R.drawable.mist
            } else {
                R.drawable.mist_night
            }
        }
        else -> {
            return R.drawable.hail
        }
    }
    return R.drawable.hail
}

