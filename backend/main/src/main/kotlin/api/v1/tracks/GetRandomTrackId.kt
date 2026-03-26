package com.inwave.backend.api.v1.tracks

import com.inwave.domain.usecase.track.query.GetRandomTrackUseCase
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.getRandomTrackId(
    getRandomTrack: GetRandomTrackUseCase
) {
    get("tracks/random/id") {
        getRandomTrack().onSuccess {
            call.respond(it.id)
        }.onFailure {
            call.respond(mapOf("error" to it.message))
        }
    }
}