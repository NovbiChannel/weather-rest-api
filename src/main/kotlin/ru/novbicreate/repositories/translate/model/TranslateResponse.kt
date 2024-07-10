package ru.novbicreate.repositories.translate.model

import kotlinx.serialization.Serializable

@Serializable
data class TranslateResponse(
    val responseData: ResponseData,
)