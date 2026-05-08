package com.inwave.backend.api.audio

import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.http.content.staticFiles
import io.ktor.server.routing.routing
import java.io.File

fun Application.staticContent() {
    routing {
        staticFiles("/images", File("src/files/images"))

        staticFiles("/audio", File("src/files/audio")) {
            contentType {
                ContentType.Audio.MPEG
            }
        }
    }
}