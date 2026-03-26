package com.inwave.backend.api.v1.tracks

import com.inwave.backend.map.toGetRandomTrackResponse
import com.inwave.domain.usecase.track.query.GetRandomTrackUseCase
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable

@Serializable
data class GetRandomTrackResponse(
    val id: String,
    val name: String,
    val playCount: Long,
    val hasLyrics: Boolean,
    val syncedText: Map<Long, String>
)

fun Route.getRandomTrack(
    getRandomTrack: GetRandomTrackUseCase
) {
    get("tracks/random") {
        getRandomTrack().onSuccess {
            call.respond(it.toGetRandomTrackResponse())
        }.onFailure {
            call.respond(mapOf("error" to it.message))
        }
    }
}