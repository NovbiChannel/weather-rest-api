package ru.novbicreate.utils

object ApiRoutes {
    private const val METRIC_BASE_URL = "http://127.0.0.1:8081"
    const val GET_JOKE = "https://geek-jokes.sameerkumar.website/api"
    const val GET_TRANSLATE = "https://api.mymemory.translated.net/get"
    const val GET_WEATHER = "https://api.openweathermap.org/data/2.5/weather"
    const val GET_CITY_COORDINATES = "https://catalog.api.2gis.com/3.0/items/geocode"
    const val POST_ERROR_METRIC = "$METRIC_BASE_URL/error_collection"
}