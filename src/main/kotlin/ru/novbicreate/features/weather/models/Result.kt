package ru.novbicreate.features.weather.models

import kotlinx.serialization.Serializable

@Serializable
data class Result(
    val items: List<Item>,
    val total: Int
)