package com.inwave.domain.entity

data class User(
    val id: String,
    val name: String,
    val role: Role,
    val isAuthenticated: Boolean
) {
    data class AuthTokens(
        val userId: String,
        val accessToken: String,
        val refreshToken: String,
        val expiresIn: Long
    )

    enum class Role {
        USER,
        ARTIST,
        MODERATOR,
        ADMIN
    }
}