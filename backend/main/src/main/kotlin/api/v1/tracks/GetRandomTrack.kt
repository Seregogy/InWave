package com.inwave.backend.api.v1.tracks

import com.inwave.api.dto.map.toFullTrackDto
import com.inwave.domain.service.TrackAudioProviderService
import com.inwave.domain.usecase.track.query.GetRandomTrackUseCase
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.getRandomTrack(
    getRandomTrack: GetRandomTrackUseCase,
    trackAudioProviderService: TrackAudioProviderService
) {
    get("/random") {
        getRandomTrack().onSuccess {
            call.respond(
                it.toFullTrackDto().copy(
                    audioUrl = trackAudioProviderService.provideUrl(it.id)
                )
            )
        }.onFailure {
            call.respond(mapOf("error" to it.message))
        }
    }
}