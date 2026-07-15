package com.inwave.backend.api.v1

import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import java.time.LocalDateTime

fun Route.status() {
    get {
        call.respond(
            mapOf(
                "status" to "OK",
                "timestamp" to LocalDateTime.now().toString()
            )
        )
    }
}