package com.inwave.backend.api.v1.artists

import com.inwave.api.dto.ErrorResponse
import com.inwave.api.dto.map.toFullArtistDto
import com.inwave.domain.usecase.artist.query.GetArtistUseCase
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Route.getArtist(
    getArtistUseCase: GetArtistUseCase
) {
    get("/{id}") {
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

        getArtistUseCase(id).onSuccess {
            call.respond(
                it.toFullArtistDto()
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