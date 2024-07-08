package ru.novbicreate.plugins

import io.ktor.server.application.*
import ru.novbicreate.features.jokes.configureJokeRouting
import ru.novbicreate.features.weather.configureWeatherRouting

fun Application.configureRouting() {
    configureWeatherRouting()
}
