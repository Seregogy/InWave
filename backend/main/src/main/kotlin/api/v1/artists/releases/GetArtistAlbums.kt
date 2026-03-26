package com.inwave.backend.api.v1.artists.releases

import com.inwave.api.dto.ErrorResponse
import com.inwave.api.dto.map.toReleaseSummaryDto
import com.inwave.domain.usecase.artist.query.GetArtistAlbumsUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.path
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Route.getArtistAlbums(
    getArtistAlbumsUseCase: GetArtistAlbumsUseCase
) {
    get("/{id}/releases/albums") {
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

        getArtistAlbumsUseCase(id).onSuccess { releases ->
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
                    message = it.message ?: "Failed to get artist",
                    path = call.request.path(),
                    timestamp = Clock.System.now().toString()
                )
            )
        }
    }
}