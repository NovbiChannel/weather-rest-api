package ru.novbicreate.features.weather

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.novbicreate.features.weather.models.WeatherRespond
import ru.novbicreate.repositories.api.ApiRepository
import ru.novbicreate.repositories.metric.MetricRepository
import ru.novbicreate.utils.Resource

class WeatherController(private val applicationCall: ApplicationCall): KoinComponent {

    companion object {
        private const val LANGUAGE_PARAM = "language"
        private const val CITY_PARAM = "city"
    }

    private val _repository: ApiRepository by inject()
    private val _metricRepository: MetricRepository by inject()

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
}