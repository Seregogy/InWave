package com.inwave.backend.api.v1.tracks

import com.inwave.api.dto.ErrorResponse
import com.inwave.api.dto.map.toFullTrackDto
import com.inwave.domain.service.TrackAdditionalDataService
import com.inwave.domain.service.TrackAudioProviderService
import com.inwave.domain.usecase.track.command.server.FetchAdditionalDataUseCase
import com.inwave.domain.usecase.track.query.GetTrackUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.path
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Route.getAdditionalTrackData(
    fetchAdditionalDataUseCase: FetchAdditionalDataUseCase,
    trackAudioProviderService: TrackAudioProviderService
) {
    get("{id}/details") {
        val id = call.parameters["id"] ?: run {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    status = HttpStatusCode.BadRequest.value,
                    message = "Track ID is required",
                    path = call.request.path(),
                    timestamp = Clock.System.now().toString()
                )
            )
            return@get
        }

        fetchAdditionalDataUseCase(id).onSuccess {
            call.respond(it.toFullTrackDto().copy(
                audioUrl = trackAudioProviderService.provideUrl(id)
            ))
        }.onFailure {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    status = HttpStatusCode.InternalServerError.value,
                    message = (it.message + it.stackTrace.joinToString("\n")) ?: "Failed to receive track data",
                    path = call.request.path(),
                    timestamp = Clock.System.now().toString()
                )
            )
        }
    }
}