package ru.hse.driverhelper.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import ru.hse.driverhelper.model.WeatherEntity

@Service
class WeatherService(
    private val restTemplate: RestTemplate,
    @Value("\${weather.token}")
    private val weatherToken: String,
    @Value("\${weather.url}")
    private val weatherUrl: String,

    ) {
    // private val simpleOutingCache = SimpleOutingCache<Pair<String, String>, WeatherEntity>()

    fun getWeather(lat: String, lon: String, fake: Boolean = false): WeatherEntity? {
        if (fake) return fakeWeather

        val url = UriComponentsBuilder.fromUriString(weatherUrl)
            .queryParam("exclude", "minutely,hourly,daily")
            .queryParam("lat", lat)
            .queryParam("lon", lon)
            .queryParam("APPID", weatherToken)
            .build().toUri()
        val res = restTemplate.getForEntity(url, WeatherEntity::class.java).body
        return res?.copy(main = WeatherEntity.Main(res.main.temp - 273.15, res.main.feelsLike - 273.15))
    }

    companion object {
        val fakeWeather = WeatherEntity(
            listOf(
                WeatherEntity.Weather(
                    id = 803,
                    main = "Clouds",
                    description = "broken clouds",
                    icon = "04n"
                )
            ),
            WeatherEntity.Main(10.0, 10.0)
        )
    }
}

