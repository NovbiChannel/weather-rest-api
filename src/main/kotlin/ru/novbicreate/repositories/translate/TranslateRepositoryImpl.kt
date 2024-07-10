package ru.novbicreate.repositories.translate

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import ru.novbicreate.repositories.translate.model.TranslateResponse
import ru.novbicreate.utils.ApiRoutes

class TranslateRepositoryImpl(private val client: HttpClient): TranslateRepository {

    override suspend fun translateText(text: String, language: String): String? =
        runCatching {
            val translateResponse = client.get(ApiRoutes.GET_TRANSLATE) {
                header(HttpHeaders.UserAgent, "Mozilla/5.0")
                parameter("q", text)
                parameter("langpair", "eu|${getLanguageCode(language)}")
            }.body<TranslateResponse>()
            translateResponse.responseData.translatedText
        }.getOrNull()

    private fun getLanguageCode(language: String?): String {
        return when (language) {
            "russian" -> "ru"
            "english" -> "eu"
            "spanish" -> "es"
            else -> "eu"
        }
    }
}