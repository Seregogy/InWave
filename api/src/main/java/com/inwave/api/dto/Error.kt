package com.inwave.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val status: Int,
    val message: String,
    val path: String,
    val timestamp: String,
    val details: Map<String, String>? = null
)