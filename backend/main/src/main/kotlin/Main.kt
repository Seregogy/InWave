package com.inwave.backend

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(Netty, port = 8000) {
        routing {
            get("/") {
                call.respondText("Hey from InWave-Backend!")
            }
        }
    }.start(wait = true)
}