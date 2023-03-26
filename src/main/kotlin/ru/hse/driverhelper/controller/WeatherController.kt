package ru.hse.driverhelper.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.hse.driverhelper.model.WeatherEntity
import ru.hse.driverhelper.service.WeatherService

@RestController
@RequestMapping("weather")
class WeatherController(
    private val weatherService: WeatherService
) {
    @GetMapping
    fun getWeather(
        @RequestParam("lat") lat: String,
        @RequestParam("lon") lon: String,
        @RequestParam("fake") fake: Boolean = false,
    ): WeatherEntity? = weatherService.getWeather(lat, lon, fake)
}
