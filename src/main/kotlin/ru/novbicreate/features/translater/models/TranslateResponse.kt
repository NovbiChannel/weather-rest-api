package ru.novbicreate.features.translater.models

import kotlinx.serialization.Serializable

@Serializable
data class TranslateResponse(
    val responseData: ResponseData,
)