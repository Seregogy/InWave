package com.inwave.domain.repository.command

import com.inwave.domain.entity.User

interface UserCommandRepository {
    suspend fun register(userName: String, password: String): Result<User.Token>
    suspend fun login(userName: String, password: String): Result<User.Token>
}