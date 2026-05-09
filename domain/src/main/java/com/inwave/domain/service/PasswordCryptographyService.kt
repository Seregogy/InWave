package com.inwave.domain.service

interface PasswordCryptographyService {
    fun hashPassword(rawPassword: String): String
    fun validatePassword(rawPassword: String, passwordHash: String): Boolean
}