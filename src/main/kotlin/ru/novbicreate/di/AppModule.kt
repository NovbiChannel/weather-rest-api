package ru.novbicreate.di

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import ru.novbicreate.repositories.api.ApiRepository
import ru.novbicreate.repositories.api.ApiRepositoryImpl
import ru.novbicreate.repositories.metric.MetricRepository
import ru.novbicreate.repositories.metric.MetricRepositoryImpl
import ru.novbicreate.repositories.translate.TranslateRepository
import ru.novbicreate.repositories.translate.TranslateRepositoryImpl

val appModule = module {
    single <HttpClient> { HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
    } }
    single<ApiRepository> { ApiRepositoryImpl (get<HttpClient>()) }
    single<MetricRepository> { MetricRepositoryImpl (get<HttpClient>()) }
    single<TranslateRepository> { TranslateRepositoryImpl (get<HttpClient>()) }
}