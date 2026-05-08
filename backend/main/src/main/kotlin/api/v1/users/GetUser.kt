package com.inwave.backend.api.v1.users

import com.inwave.api.dto.ErrorResponse
import com.inwave.api.dto.user.fromDomain
import com.inwave.domain.repository.query.UserQueryRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.path
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.getUser(
    userRepository: UserQueryRepository
) {
    authenticate("user-auth-jwt") {
        get("/") {
            val userId = call.principal<JWTPrincipal>()?.payload
                ?.getClaim("userId")
                ?.asString()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        status = HttpStatusCode.BadRequest.value,
                        message = "Invalid user id",
                        path = call.request.path(),
                        timestamp = System.currentTimeMillis().toString()
                    )
                )

                userRepository.getUser(userId).onSuccess {
                    call.respond(
                        it.copy(isAuthenticated = true)
                            .fromDomain()
                    )
                }.onFailure {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(
                            status = HttpStatusCode.BadRequest.value,
                            message = it.message ?: "Failed to fetch user",
                            path = call.request.path(),
                            timestamp = System.currentTimeMillis().toString()
                        )
                    )
                }
            }

        get("/{id}") {
            val userId = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    status = HttpStatusCode.BadRequest.value,
                    message = "Invalid user id",
                    path = call.request.path(),
                    timestamp = System.currentTimeMillis().toString()
                )
            )

            userRepository.getUser(userId).onSuccess {
                call.respond(it.fromDomain())
            }.onFailure {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        status = HttpStatusCode.BadRequest.value,
                        message = it.message ?: "Failed to fetch user",
                        path = call.request.path(),
                        timestamp = System.currentTimeMillis().toString()
                    )
                )
            }
        }
    }
}