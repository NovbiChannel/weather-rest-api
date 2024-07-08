package ru.novbicreate.features.translater

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import ru.novbicreate.features.metric.MetricService
import ru.novbicreate.features.translater.models.TranslateResponse
import ru.novbicreate.utils.ApiRoutes

class TranslateService(private val language: String?, private val client: HttpClient) {
    private val metricService = MetricService(client)
    suspend fun translateText(text: String): String? {
        try {
            val translateResponse: HttpResponse = client.get(ApiRoutes.GET_TRANSLATE) {
                header("User-Agent", "Mozilla/5.0")
                parameter("q", text)
                parameter("langpair", "eu|${getLanguageCode(language)}")
            }
            val translateJoke: TranslateResponse = translateResponse.body()
            return translateJoke.responseData.translatedText
        } catch (e: Exception) {
            e.printStackTrace()
            metricService.sendError(e)
            return null
        }
    }
    private fun getLanguageCode(language: String?): String {
        return when (language) {
            "russian" -> "ru"
            "english" -> "eu"
            "spanish" -> "es"
            else -> "eu"
        }
    }
}