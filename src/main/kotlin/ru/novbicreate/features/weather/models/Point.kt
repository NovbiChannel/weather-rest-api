package ru.novbicreate.features.weather.models

import kotlinx.serialization.Serializable

@Serializable
data class Point(
    val lat: Double,
    val lon: Double
)