package ru.novbicreate.features.weather.models

import kotlinx.serialization.Serializable

@Serializable
data class BotMetadata(
    val welcomeMessage: String = "Welcome to our weather bot! \uD83D\uDC4B " +
            "Write the name of the city for which you want to find out the weather " +
            "(for example, \"Moscow\" or \"Vladivostok\") and I will try to find it. " +
            "I will be glad if you use my weather search services again! \uD83D\uDE42",
    val additionalMessage: String = "If you want to find out the weather in another city, just send its name.",
    val connectionErrorMessage: String = "Connection to the server is lost, try again later \uD83D\uDE22",
    val timeoutErrorMessage: String = "The waiting time for a response from the server has expired. Please try again.",
    val illegalArgumentErrorMessage: String = "I do not know such a city. Please check the name of the city and try again.",
    val unknownError: String = "I'm sorry, I didn't understand. Please send the name of the city where you want to find out the weather.",
    val weatherTitle: String = "Weather in the city",
    val humidity: String = "Humidity",
    val wind: String = "Wind",
    val ms: String = "m/s"
)