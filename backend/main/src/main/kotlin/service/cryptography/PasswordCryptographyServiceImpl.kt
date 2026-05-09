package com.inwave.backend.service.cryptography

import com.inwave.domain.service.PasswordCryptographyService
import org.mindrot.jbcrypt.BCrypt

class PasswordCryptographyServiceBCrypt : PasswordCryptographyService {
    override fun hashPassword(rawPassword: String): String {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt())
    }

    override fun validatePassword(
        rawPassword: String,
        passwordHash: String
    ): Boolean {
        return BCrypt.checkpw(rawPassword, passwordHash)
    }
}