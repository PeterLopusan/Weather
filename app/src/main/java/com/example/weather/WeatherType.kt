package com.example.weather

enum class WeatherType {
    CLOUDY, FREEZING, SUNNY_CLEAR, MIST, PARTLY_CLOUD, RAIN, SNOW, CLEAR, HAIL;

    companion object {
        fun getType(weatherCode: String): WeatherType {
            when (weatherCode) {
                "1000" -> {
                    return SUNNY_CLEAR
                }

                "1135", "1030", "1147" -> {
                    return MIST
                }

                "1003" -> {
                    return PARTLY_CLOUD
                }

                "1006", "1009" -> {
                    return CLOUDY
                }

                "1237", "1261", "1264" -> {
                    return HAIL
                }

                "1150", "1171", "1072" -> {
                    return FREEZING
                }

                "1066", "1210", "1213", "1216", "1219", "1222", "1114", "1117", "1225", "1255", "1258", "1279", "1282" -> {
                    return SNOW
                }

                else -> {
                    return RAIN
                }
            }
        }
    }
}