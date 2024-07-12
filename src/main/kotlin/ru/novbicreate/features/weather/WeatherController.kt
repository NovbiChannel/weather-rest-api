package ru.novbicreate.features.weather

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.novbicreate.features.weather.models.BotMetadata
import ru.novbicreate.features.weather.models.WeatherRespond
import ru.novbicreate.repositories.api.ApiRepository
import ru.novbicreate.repositories.metric.MetricRepository
import ru.novbicreate.repositories.translate.TranslateRepository
import ru.novbicreate.utils.Resource

class WeatherController(private val applicationCall: ApplicationCall): KoinComponent {

    companion object {
        private const val LANGUAGE_PARAM = "language"
        private const val CITY_PARAM = "city"
    }

    private val _repository: ApiRepository by inject()
    private val _metricRepository: MetricRepository by inject()
    private val _translateRepository: TranslateRepository by inject()
    private val _scope = CoroutineScope(Dispatchers.IO)

    suspend fun fetchWeather() {
        val language = applicationCall.request.queryParameters[LANGUAGE_PARAM]
        val city = applicationCall.request.queryParameters[CITY_PARAM]
        if (language != null && city != null) {
            when (val respond = _repository.generateWeatherRespond(language, city)) {
                is Resource.Success -> {
                    val response: WeatherRespond = respond.data!!
                    applicationCall.respond(response)
                }

                is Resource.Error -> {
                    val message = respond.message?: "Uncknown error"
                    _metricRepository.sendError(message)
                    applicationCall.respond(HttpStatusCode.InternalServerError, message)
                }
            }
        } else {
            val missingParams = mutableListOf<String>()
            if (language == null) missingParams.add(LANGUAGE_PARAM)
            if (city == null) missingParams.add(CITY_PARAM)
            val message = "Missing required parameter(s): ${missingParams.joinToString(", ")}"
            applicationCall.respond(HttpStatusCode.BadRequest, message)
        }
    }
    suspend fun fetchBotMetadata() {
        val language = applicationCall.request.queryParameters[LANGUAGE_PARAM]
        language?.let {
            val metadata = BotMetadata()
            val welcomeMessage = _scope.async { _translateRepository.translateText(metadata.welcomeMessage, it) }
            val additionalMessage = _scope.async { _translateRepository.translateText(metadata.additionalMessage, it) }
            val connectionErrorMessage = _scope.async { _translateRepository.translateText(metadata.connectionErrorMessage, it) }
            val timeoutErrorMessage = _scope.async { _translateRepository.translateText(metadata.timeoutErrorMessage, it) }
            val illegalArgumentErrorMessage = _scope.async { _translateRepository.translateText(metadata.illegalArgumentErrorMessage, it) }
            val unknownError = _scope.async { _translateRepository.translateText(metadata.unknownError, it) }
            val weatherTitle = _scope.async { _translateRepository.translateText(metadata.weatherTitle, it) }
            val humidity = _scope.async { _translateRepository.translateText(metadata.humidity, it) }
            val wind = _scope.async { _translateRepository.translateText(metadata.wind, it) }
            val ms = _scope.async { _translateRepository.translateText(metadata.ms, it) }
            applicationCall.respond(BotMetadata(
                welcomeMessage = welcomeMessage.await()?: metadata.welcomeMessage,
                additionalMessage = additionalMessage.await()?: metadata.additionalMessage,
                connectionErrorMessage = connectionErrorMessage.await()?: metadata.connectionErrorMessage,
                timeoutErrorMessage = timeoutErrorMessage.await()?: metadata.timeoutErrorMessage,
                illegalArgumentErrorMessage = illegalArgumentErrorMessage.await()?: metadata.illegalArgumentErrorMessage,
                unknownError = unknownError.await()?: metadata.unknownError,
                weatherTitle = weatherTitle.await()?: metadata.weatherTitle,
                humidity = humidity.await()?: metadata.humidity,
                wind = wind.await()?: metadata.wind,
                ms = ms.await()?: metadata.ms
            ))
        }?: run {
            val message = "Missing required parameter(s): $LANGUAGE_PARAM}"
            applicationCall.respond(HttpStatusCode.BadRequest, message)
        }
    }
}