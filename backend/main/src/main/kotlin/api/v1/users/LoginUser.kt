package com.inwave.backend.api.v1.users

import com.inwave.api.dto.ErrorResponse
import com.inwave.api.dto.user.UserLoginResponse
import com.inwave.api.dto.user.UserRegisterRequest
import com.inwave.domain.repository.command.UserCommandRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.path
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.loginUser(
    userRepository: UserCommandRepository
) {
    post("/login") {
        val request = call.receive<UserRegisterRequest>()

        userRepository.login(
            request.userName,
            request.password
        ).onSuccess {
            call.respond(
                UserLoginResponse(
                    it.token,
                    it.expiredAt
                )
            )
        }.onFailure {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    status = HttpStatusCode.InternalServerError.value,
                    message = it.message ?: "Failed to register user",
                    path = call.request.path(),
                    timestamp = System.currentTimeMillis().toString()
                )
            )
        }
    }
}