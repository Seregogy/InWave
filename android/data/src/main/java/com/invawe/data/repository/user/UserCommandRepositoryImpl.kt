package com.invawe.data.repository.user

import com.inwave.api.dto.ErrorResponse
import com.inwave.api.dto.user.UserLoginRequest
import com.inwave.api.dto.user.UserLoginResponse
import com.inwave.api.dto.user.UserRegisterRequest
import com.inwave.api.dto.user.UserRegisterResponse
import com.inwave.domain.entity.User
import com.inwave.domain.repository.command.UserCommandRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class UserCommandRepositoryImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = "/v1"
) : UserCommandRepository {
    override suspend fun register(
        userName: String,
        password: String
    ): Result<User.Token> = runCatching {
        val response = httpClient.post("$baseUrl/users/register") {
            contentType(ContentType.Application.Json)
            setBody(UserRegisterRequest(userName, password))

        }

         if (response.status != HttpStatusCode.Created) {
             throw Exception(response.body<ErrorResponse>().message)
         }

        response.body<UserRegisterResponse>().let {
            User.Token(
                it.token,
                it.expiredAt
            )
        }
    }

    override suspend fun login(
        userName: String,
        password: String
    ): Result<User.Token> = runCatching {
        val response = httpClient.post("$baseUrl/users/login") {
            contentType(ContentType.Application.Json)
            setBody(UserLoginRequest(userName, password))
        }

        if (response.status != HttpStatusCode.OK) {
            throw Exception(response.body<ErrorResponse>().message)
        }

        response.body<UserLoginResponse>().let {
            User.Token(
                it.token,
                it.expiredAt
            )
        }
    }
}