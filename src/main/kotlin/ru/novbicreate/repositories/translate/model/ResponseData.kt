package ru.novbicreate.repositories.translate.model

import kotlinx.serialization.Serializable

@Serializable
data class ResponseData(
    val translatedText: String
)