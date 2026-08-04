package com.inwave.backend.api.rabbitmq

import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.exchangeDeclare
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.queueBind
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.queueDeclare
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.rabbitmq
import io.ktor.server.application.Application

fun Application.setupTrackAdditionalDataExchange() {
    rabbitmq {
        exchangeDeclare {
            exchange = "track-additional-data-exchange"
            type = "direct"
        }

        queueBind {
            queue = "track-additional-data-request"
            exchange = "track-additional-data-exchange"

            routingKey = "track.data.request"

            queueDeclare {
                queue = "track-additional-data-request"
                durable = true
            }
        }

        queueBind {
            queue = "track-additional-data-response"
            exchange = "track-additional-data-exchange"

            routingKey = "track.data.response"

            queueDeclare {
                queue = "track-additional-data-response"
                durable = true
            }
        }
    }
}
