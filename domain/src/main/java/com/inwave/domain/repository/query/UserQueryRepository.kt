package com.inwave.domain.repository.query

import com.inwave.domain.entity.User

interface UserQueryRepository {
    fun getUser(userId: String): Result<User>
    fun getUserByToken(token: String): Result<User>
    fun login(token: String): Result<String>
}