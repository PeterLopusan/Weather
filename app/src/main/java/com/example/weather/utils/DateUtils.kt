package com.example.weather.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun formatDateForHourlyAdapter(date: String): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd kk:mm", Locale.getDefault())
        val currentDate = simpleDateFormat.parse(date)

        val newSimpleDateFormat = SimpleDateFormat("kk:mm", Locale.getDefault())
        val formattedDate = newSimpleDateFormat.format(currentDate)

        return formattedDate.toString()
    }

    fun formatDateForForecast(date: String): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = simpleDateFormat.parse(date)

        val newSimpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val formattedDate = newSimpleDateFormat.format(currentDate)

        return formattedDate.toString()
    }

    fun formatTimeTo24Format(time: String): String {
        val format24 = SimpleDateFormat("HH:mm")
        val format12 = SimpleDateFormat("hh:mm a")
        val formattedTime = format12.parse(time)
        return format24.format(formattedTime).toString()
    }

    fun getCurrentTime(): String {
        val formatter = SimpleDateFormat("HH:mm")
        val calendar = Calendar.getInstance()
        return formatter.format(calendar.timeInMillis)
    }

    fun getAfterTomorrowDay(): String {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("EEEE", Locale.getDefault())
        calendar.add(Calendar.DATE, 2)
        return formatter.format(calendar.time)
    }
}
