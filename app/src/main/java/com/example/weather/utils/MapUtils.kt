package com.example.weather.utils

import android.content.Context
import com.google.android.gms.maps.GoogleMap

fun setMap(map: GoogleMap, context: Context) {
    map.mapType = if (SharedPreferencesUtils.getMapType(context) == SharedPreferencesUtils.defaultMapType) {
        GoogleMap.MAP_TYPE_NORMAL
    } else {
        GoogleMap.MAP_TYPE_HYBRID
    }
}