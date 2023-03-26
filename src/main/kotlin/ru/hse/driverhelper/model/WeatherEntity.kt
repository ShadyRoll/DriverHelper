package ru.hse.driverhelper.model

import com.fasterxml.jackson.annotation.JsonProperty

data class WeatherEntity(val weather: List<Weather>, val main: Main) {

    data class Weather(
        val id: Long,
        val main: String,
        val description: String,
        val icon: String,
    )

    data class Main(
        val temp: Double,
        @JsonProperty("feels_like")
        val feelsLike: Double,
    )
}