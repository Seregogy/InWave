package com.inwave.backend.api.v1.tracks

import com.inwave.api.dto.map.toTrackSummaryDto
import com.inwave.domain.usecase.track.query.GetRandomTrackUseCase
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.getRandomTrack(
    getRandomTrack: GetRandomTrackUseCase
) {
    get("/random") {
        getRandomTrack().onSuccess {
            call.respond(it.toTrackSummaryDto())
        }.onFailure {
            call.respond(mapOf("error" to it.message))
        }
    }
}