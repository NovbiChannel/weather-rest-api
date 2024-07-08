package ru.novbicreate.features.weather.models

import kotlinx.serialization.Serializable

@Serializable
data class TwoGisResult(
    val meta: Meta,
    val result: Result
)