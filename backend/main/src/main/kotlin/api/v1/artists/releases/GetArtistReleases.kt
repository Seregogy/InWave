package com.inwave.backend.api.v1.artists.releases

import com.inwave.api.dto.ErrorResponse
import com.inwave.api.dto.map.toReleaseSummaryDto
import com.inwave.domain.usecase.artist.query.GetArtistReleasesUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.path
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Route.getArtistReleases(
    getArtistReleasesUseCase: GetArtistReleasesUseCase
) {
    get("/{id}/releases") {
        val id = call.parameters["id"] ?: run {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    status = HttpStatusCode.BadRequest.value,
                    message = "Artist ID is required",
                    path = call.request.path(),
                    timestamp = Clock.System.now().toString()
                )
            )
            return@get
        }

        getArtistReleasesUseCase(id).onSuccess { releases ->
            call.respond(
                releases.map {
                    it.toReleaseSummaryDto()
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