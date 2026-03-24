package com.inwave.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: String,
    val details: Map<String, String>? = null
)