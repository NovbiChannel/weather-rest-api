package ru.novbicreate.features.weather.models

import kotlinx.serialization.Serializable

@Serializable
data class Sys(
    val country: String? = null,
    val id: Int? = 0,
    val sunrise: Int? = 0,
    val sunset: Int? = 0,
    val type: Int? = 0
)