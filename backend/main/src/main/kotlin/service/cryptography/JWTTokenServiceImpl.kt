package com.inwave.backend.service.cryptography

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import com.inwave.domain.service.JWTTokenService
import io.github.cdimascio.dotenv.Dotenv
import java.util.Date
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit

class JWTTokenServiceImpl(
    private val dotenv: Dotenv,
    private val verifier: JWTVerifier
) : JWTTokenService {
    override fun generateToken(userId: String): Pair<String, Date> {
        val expireDate = Date(System.currentTimeMillis().plus(
            7.days.toLong(DurationUnit.MILLISECONDS)
        ))

        val token = JWT.create()
            .withClaim("userId", userId)
            .withIssuer(dotenv["DOMAIN_URL"])
            .withExpiresAt(expireDate)
            .sign(Algorithm.HMAC256(dotenv["JWT_SECRET"]))

        return token to expireDate
    }

    override fun extractId(token: String): Result<String> = runCatching {
        verifier.verify(token)
            .getClaim("userId")
            .asString()
    }
}