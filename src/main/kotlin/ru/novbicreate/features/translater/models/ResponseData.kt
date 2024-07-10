package ru.novbicreate.features.translater.models

import kotlinx.serialization.Serializable

@Serializable
data class ResponseData(
    val match: Double,
    val translatedText: String
)