package ru.novbicreate.features.weather.models

import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    val api_version: String,
    val code: Int,
    val issue_date: String
)