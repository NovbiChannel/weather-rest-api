package ru.novbicreate.repositories.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.novbicreate.features.weather.models.Point
import ru.novbicreate.features.weather.models.TwoGisResult
import ru.novbicreate.features.weather.models.WeatherRespond
import ru.novbicreate.features.weather.models.WeatherResponse
import ru.novbicreate.repositories.translate.TranslateRepository
import ru.novbicreate.repositories.translate.TranslateRepositoryImpl
import ru.novbicreate.utils.ApiRoutes.GET_CITY_COORDINATES
import ru.novbicreate.utils.ApiRoutes.GET_WEATHER
import ru.novbicreate.utils.Resource

class ApiRepositoryImpl(private val client: HttpClient): ApiRepository, KoinComponent {

    companion object {
        private const val TWO_GIS_API_KEY_ENV = "TWO_GIS_KEY"
        private const val WEATHER_API_KEY_ENV = "API_KEY"
        private const val LATITUDE_PARAM = "lat"
        private const val LONGITUDE_PARAM = "lon"
        private const val APP_ID_PARAM = "appid"
        private const val QUERY_PARAM = "q"
        private const val FIELDS_PARAM = "fields"
        private const val KEY_PARAM = "key"
    }
    private val _translateRepository: TranslateRepository by inject()
    private val _twoGisApiKey = System.getenv(TWO_GIS_API_KEY_ENV)
    private val _weatherApiKey = System.getenv(WEATHER_API_KEY_ENV)

    override suspend fun generateWeatherRespond(language: String, city: String): Resource<WeatherRespond> {
        return try {
            val cityCoordinates = fetchCityCoordinates(city)
            val weatherData = fetchWeatherData(cityCoordinates.lat, cityCoordinates.lon)
            Resource.Success(respondWithWeatherData(weatherData, language))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.localizedMessage)
        }
    }

    private suspend fun fetchCityCoordinates(cityName: String): Point =
        client.get(GET_CITY_COORDINATES) {
            parameter(QUERY_PARAM, cityName)
            parameter(FIELDS_PARAM, "items.point")
            parameter(KEY_PARAM, _twoGisApiKey)
        }.body<TwoGisResult>().result.items.first().point

    private suspend fun fetchWeatherData(latitude: Double, longitude: Double): WeatherResponse =
        client.get(GET_WEATHER) {
            parameter(LATITUDE_PARAM, latitude.toString())
            parameter(LONGITUDE_PARAM, longitude.toString())
            parameter(APP_ID_PARAM, _weatherApiKey)
        }.body()

    private suspend fun respondWithWeatherData(weatherData: WeatherResponse, language: String): WeatherRespond {
        val city = weatherData.name
        val weatherConditions = weatherData.weather.map { it.description }
        val conditionEmoji = getEmojiCondition(weatherConditions.first())

        val translatedCityAndConditions = _translateRepository.translateText("$city; ${weatherConditions.first()}", language)
            ?: "$city; ${weatherConditions.first()}"

        val (translatedCity, translatedConditions) = translatedCityAndConditions.split("; ")

        return WeatherRespond(
            city = translatedCity,
            conditions = translatedConditions,
            conditionsEmoji = conditionEmoji?: "",
            temperature = kelvinToCelsius(weatherData.main.temp).toInt(),
            humidity = weatherData.main.humidity,
            windSpeed = weatherData.wind?.speed?.toInt()
        )
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