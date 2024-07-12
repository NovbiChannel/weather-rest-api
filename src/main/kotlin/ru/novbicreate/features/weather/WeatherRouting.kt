package ru.novbicreate.features.weather

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureWeatherRouting() {
    routing {
        get(Endpoints.GET_WEATHER) {
            val controller = WeatherController(call)
            controller.fetchWeather()
        }
        get(Endpoints.GET_BOT_METADATA) {
            val controller = WeatherController(call)
            controller.fetchBotMetadata()
        }
    }
}