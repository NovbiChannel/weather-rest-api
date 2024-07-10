package ru.novbicreate

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.core.context.startKoin
import ru.novbicreate.di.apiModule
import ru.novbicreate.di.servicesModule
import ru.novbicreate.plugins.configureRouting
import ru.novbicreate.plugins.configureSerialization

fun main() {
    startKoin {
        modules(apiModule)
        modules(servicesModule)
    }
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureRouting()
    configureSerialization()
}
