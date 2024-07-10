package ru.novbicreate.repositories.metric

interface MetricRepository {
    suspend fun sendError(error: String)
}