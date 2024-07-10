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

val servicesModule = module {
    single<MetricRepository> {
        MetricRepositoryImpl (
            client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }
        )
    }
    single<TranslateRepository> {
        TranslateRepositoryImpl (
            client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }
        )
    }
}
val apiModule = module {
    single<ApiRepository> {
        ApiRepositoryImpl (
            client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }
        )
    }
}