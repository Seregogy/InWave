package com.inwave.backend.route.track

import com.inwave.backend.map.toGetRandomTrackResponse
import com.inwave.domain.usecase.track.GetRandomTrackUseCase
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable

@Serializable
data class GetRandomTrackResponse(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val audioUrl: String
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