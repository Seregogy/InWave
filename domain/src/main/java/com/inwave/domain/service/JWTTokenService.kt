package com.inwave.domain.service

import java.util.Date

interface JWTTokenService {
    fun generateToken(userId: String): Pair<String, Date>
    fun extractId(token: String): Result<String>
}