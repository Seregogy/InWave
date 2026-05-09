package com.inwave.domain.repository.query

import com.inwave.domain.entity.User

interface UserQueryRepository {
    suspend fun getUser(userId: String): Result<User>
    suspend fun getUserByToken(token: String): Result<User>
}