package ru.novbicreate.repositories.api

import ru.novbicreate.features.weather.models.WeatherRespond
import ru.novbicreate.utils.Resource

interface ApiRepository {
    suspend fun generateWeatherRespond(language: String, city: String): Resource<WeatherRespond>
}