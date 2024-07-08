package ru.novbicreate.features.weather.models

import kotlinx.serialization.Serializable

@Serializable
data class Rain(
    val `1h`: Double
)