package com.inwave.domain.auth

interface AuthClientRepository {
    suspend fun register(userName: String, password: String): Result<Unit>
    suspend fun login(userName: String, password: String): Result<Unit>
    suspend fun logout(): Result<Unit>
}