package ru.novbicreate.repositories.metric

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import ru.novbicreate.repositories.metric.model.ErrorData
import ru.novbicreate.utils.ApiRoutes

class MetricRepositoryImpl(private val client: HttpClient): MetricRepository {
    override suspend fun sendError(error: String) {
        try {
            client.post(ApiRoutes.POST_ERROR_METRIC) {
                contentType(ContentType.Application.Json)
                setBody(
                    ErrorData(
                        type = "server",
                        source = "weather_API",
                        System.currentTimeMillis(),
                        error
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }
}