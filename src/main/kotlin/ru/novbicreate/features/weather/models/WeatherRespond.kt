package ru.novbicreate.features.weather.models

import kotlinx.serialization.Serializable

@Serializable
data class WeatherRespond(
    val city: String,
    val conditions: String,
    val temperature: Int,
    val humidity: Int,
    val windSpeed: Int? = 0,
)
