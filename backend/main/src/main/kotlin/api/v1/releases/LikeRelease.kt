package com.inwave.backend.api.v1.releases

import com.inwave.api.dto.ErrorResponse
import com.inwave.api.dto.release.ReleaseToggleLikeResponse
import com.inwave.domain.repository.command.LikeRepository
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.path
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.likeRelease(
    likeRepository: LikeRepository
) {
    authenticate("user-auth-jwt") {
        post("/{id}/like") {
            val releaseId = call.parameters["id"]
                ?: return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        status = HttpStatusCode.BadRequest.value,
                        message = "Invalid id",
                        path = call.request.path(),
                        timestamp = System.currentTimeMillis().toString()
                    )
                )

            val token = call.request.headers[HttpHeaders.Authorization]
                ?.removePrefix("Bearer ")
                ?.trim()
                ?: return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        status = HttpStatusCode.BadRequest.value,
                        message = "Invalid token",
                        path = call.request.path(),
                        timestamp = System.currentTimeMillis().toString()
                    )
                )

            likeRepository.toggleLikeToRelease(token, releaseId).onSuccess {
                call.respond(ReleaseToggleLikeResponse(it))
            }.onFailure {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        status = HttpStatusCode.BadRequest.value,
                        message = it.message ?: "",
                        path = call.request.path(),
                        timestamp = System.currentTimeMillis().toString()
                    )
                )
            }
        }
    }
}
