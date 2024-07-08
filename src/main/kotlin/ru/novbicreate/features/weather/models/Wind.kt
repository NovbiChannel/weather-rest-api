package ru.novbicreate.features.weather.models

import kotlinx.serialization.Serializable

@Serializable
data class Wind(
    val deg: Int,
    val speed: Double
)