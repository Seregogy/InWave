package com.inwave.backend.api.rabbitmq

import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.exchangeDeclare
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.queueBind
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.queueDeclare
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.rabbitmq
import io.ktor.server.application.Application

fun Application.setupLyricsExchange() {
    rabbitmq {
        exchangeDeclare {
            exchange = "track-lyrics-exchange"
            type = "direct"
        }

        queueBind {
            exchange = "track-lyrics-exchange"
            queue = "track-lyrics-request"

            routingKey = "track.lyrics.request"

            queueDeclare {
                queue = "track-lyrics-request"
                durable = true
            }
        }

        queueBind {
            exchange = "track-lyrics-exchange"
            queue = "track-lyrics-response"

            routingKey = "track.lyrics.response"

            queueDeclare {
                queue = "track-lyrics-response"
                durable = true
            }
        }
    }
}
