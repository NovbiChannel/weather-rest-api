package ru.novbicreate.features.metric

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import ru.novbicreate.utils.ApiRoutes

class MetricService(private val client: HttpClient) {
    suspend fun sendError(e: Exception) {
        client.post(ApiRoutes.POST_ERROR_METRIC) {
            contentType(ContentType.Application.Json)
            setBody(
                ErrorData(
                    type = "server",
                    source = "weather_API",
                    System.currentTimeMillis(),
                    e.localizedMessage
                )
            )
        }
    }
}