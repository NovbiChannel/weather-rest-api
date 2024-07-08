package ru.novbicreate.features.weather.models

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val point: Point
)