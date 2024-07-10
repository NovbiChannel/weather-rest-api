package ru.novbicreate.features.weather

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json
import ru.novbicreate.features.metric.MetricService
import ru.novbicreate.features.translater.TranslateService
import ru.novbicreate.features.weather.models.*
import ru.novbicreate.utils.ApiRoutes.GET_CITY_COORDINATES
import ru.novbicreate.utils.ApiRoutes.GET_WEATHER

class WeatherController(private val applicationCall: ApplicationCall) {
    companion object {
        private const val LANGUAGE_PARAM = "language"
        private const val CITY_PARAM = "city"
        private const val TWO_GIS_API_KEY_ENV = "TWO_GIS_KEY"
        private const val WEATHER_API_KEY_ENV = "API_KEY"
        private const val LATITUDE_PARAM = "lat"
        private const val LONGITUDE_PARAM = "lon"
        private const val APP_ID_PARAM = "appid"
        private const val QUERY_PARAM = "q"
        private const val FIELDS_PARAM = "fields"
        private const val KEY_PARAM = "key"
    }

    private val _twoGisApiKey = System.getenv(TWO_GIS_API_KEY_ENV)
    private val _weatherApiKey = System.getenv(WEATHER_API_KEY_ENV)
    private val _httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }
    private val _metricService = MetricService(_httpClient)

    suspend fun fetchWeather() {
        try {
            val language = applicationCall.request.queryParameters[LANGUAGE_PARAM]
            val cityName = applicationCall.request.queryParameters[CITY_PARAM]
            val cityCoordinates = fetchCityCoordinates(cityName)
            val weatherData = fetchWeatherData(cityCoordinates.lat.toString(), cityCoordinates.lon.toString())
            respondWithWeatherData(weatherData, language)
        } catch (exception: Exception) {
            _metricService.sendError(exception)
            applicationCall.respond(HttpStatusCode.BadGateway, "Ошибка: ${exception.message}")
        }
    }

    private suspend fun respondWithWeatherData(weatherData: WeatherResponse, language: String?) {
        val city = weatherData.name
        val weatherConditions = weatherData.weather.map { it.description }
        val conditionEmoji = getEmojiCondition(weatherConditions.first())
        val translationInput = "$city; ${weatherConditions.joinToString("; ")}"
        val translationService = TranslateService(language, _httpClient)
        val translatedText = translationService.translateText(translationInput)?: translationInput
        val translatedParts = translatedText.split("; ")
        val currentTemperatureCelsius = kelvinToCelsius(weatherData.main.temp).toInt()
        applicationCall.respond(WeatherRespond(
            city = translatedParts.first(),
            conditions = translatedParts.last(),
            conditionsEmoji = conditionEmoji?: "",
            temperature = currentTemperatureCelsius,
            humidity = weatherData.main.humidity,
            windSpeed = weatherData.wind?.speed?.toInt()
        ))
    }

    private suspend fun fetchCityCoordinates(cityName: String?): Point {
        val cityCoordinatesResponse: HttpResponse = _httpClient.get(GET_CITY_COORDINATES) {
            parameter(QUERY_PARAM, cityName)
            parameter(FIELDS_PARAM, "items.point")
            parameter(KEY_PARAM, _twoGisApiKey)
        }
        val cityData: TwoGisResult = cityCoordinatesResponse.body()
        return cityData.result.items.first().point
    }

    private suspend fun fetchWeatherData(latitude: String, longitude: String): WeatherResponse {
        val weatherResponse: HttpResponse = _httpClient.get(GET_WEATHER) {
            parameter(LATITUDE_PARAM, latitude)
            parameter(LONGITUDE_PARAM, longitude)
            parameter(APP_ID_PARAM, _weatherApiKey)
        }
        return weatherResponse.body()
    }

    private fun getEmojiCondition(condition: String): String? {
        val conditions = listOf(
            Pair("clear sky", "☀\uFE0F"),
            Pair("few clouds", "\uD83C\uDF24"),
            Pair("scattered clouds", "\uD83C\uDF25"),
            Pair("broken clouds", "☁\uFE0F"),
            Pair("shower rain", "\uD83C\uDF27"),
            Pair("rain", "\uD83C\uDF26"),
            Pair("thunderstorm", "\uD83C\uDF29"),
            Pair("snow", "❄\uFE0F"),
            Pair("mist", "\uD83D\uDE36\u200D\uD83C\uDF2B\uFE0F")
        )
        return conditions.find { it.first == condition }?.second
    }

    private fun kelvinToCelsius(kelvin: Double) = kelvin - 273.15
}