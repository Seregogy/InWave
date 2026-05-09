package com.inwave.backend.api.v1.releases

import com.inwave.api.dto.ErrorResponse
import com.inwave.api.dto.map.toFullTrackDto
import com.inwave.domain.service.TrackAudioProviderService
import com.inwave.domain.usecase.release.query.GetReleaseTracksUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.path
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Route.getReleaseTracks(
    getReleaseTracksUseCase: GetReleaseTracksUseCase,
    trackAudioProviderService: TrackAudioProviderService
) {
    get("/{id}/tracks") {
        val id = call.parameters["id"] ?: run {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    status = HttpStatusCode.BadRequest.value,
                    message = "Release ID is required",
                    path = call.request.path(),
                    timestamp = Clock.System.now().toString()
                )
            )
            return@get
        }

        getReleaseTracksUseCase(id).onSuccess { tracks ->
            call.respond(
                tracks.map {
                    it.toFullTrackDto().copy(
                        audioUrl = trackAudioProviderService.provideUrl(id)
                    )
                }
            )
        }.onFailure {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    status = HttpStatusCode.InternalServerError.value,
                    message = it.message ?: "Failed to get artist releases",
                    path = call.request.path(),
                    timestamp = Clock.System.now().toString()
                )
            )
        }
    }
}