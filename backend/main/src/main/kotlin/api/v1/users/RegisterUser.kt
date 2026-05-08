package com.inwave.backend.api.v1.users

import com.inwave.api.dto.ErrorResponse
import com.inwave.api.dto.user.UserRegisterRequest
import com.inwave.api.dto.user.UserRegisterResponse
import com.inwave.domain.repository.command.UserCommandRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.path
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.registerUser(
    userRepository: UserCommandRepository,
) {
    post("/register") {
        val request = call.receive<UserRegisterRequest>()

        userRepository.register(
            request.userName,
            request.password
        ).onSuccess {
            call.respond(
                HttpStatusCode.Created,
                UserRegisterResponse(
                    it.token,
                    it.expiredAt
                )
            )
        }.onFailure {
            call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                    status = HttpStatusCode.BadRequest.value,
                    message = it.message ?: "Failed to register user",
                    path = call.request.path(),
                    timestamp = System.currentTimeMillis().toString()
                )
            )
        }
    }
}