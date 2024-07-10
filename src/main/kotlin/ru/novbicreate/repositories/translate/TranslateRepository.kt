package ru.novbicreate.repositories.translate

interface TranslateRepository {
    suspend fun translateText(text: String, language: String): String?
}