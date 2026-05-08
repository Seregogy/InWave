package com.inwave.domain.repository.command

interface UserCommandRepository {
    fun register(userName: String, password: String): Result<String>
}