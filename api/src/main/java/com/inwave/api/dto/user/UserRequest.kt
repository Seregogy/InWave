package com.inwave.api.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterRequest(
    val userName: String,
    val password: String
)

@Serializable
data class UserLoginRequest(
    val userName: String,
    val password: String
)