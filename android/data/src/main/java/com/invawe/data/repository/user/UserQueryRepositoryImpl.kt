package com.invawe.data.repository.user

import com.inwave.api.dto.user.FullUserDto
import com.inwave.api.dto.user.toDomain
import com.inwave.domain.entity.User
import com.inwave.domain.repository.query.UserQueryRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders

class UserQueryRepositoryImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = "/v1"
) : UserQueryRepository {
    override suspend fun getUser(userId: String): Result<User> = runCatching {
        httpClient.get("$baseUrl/users/$userId")
            .body<FullUserDto>()
            .toDomain()
    }

    override suspend fun getUserByToken(token: String): Result<User> = runCatching {
        val a = httpClient.get("$baseUrl/users/") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
            .body<FullUserDto>()
            .toDomain()

        return@runCatching a
    }
}