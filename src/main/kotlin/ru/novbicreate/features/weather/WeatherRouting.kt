package ru.novbicreate.features.weather

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.core.context.startKoin
import ru.novbicreate.di.apiModule
import ru.novbicreate.di.servicesModule

fun Application.configureWeatherRouting() {
    routing {
        get(Endpoints.GET_WEATHER) {
            val controller = WeatherController(call)
            controller.fetchWeather()
        }
    }
}