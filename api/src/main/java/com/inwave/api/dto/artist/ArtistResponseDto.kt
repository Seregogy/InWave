package com.inwave.api.dto.artist

import kotlinx.serialization.Serializable

@Serializable
data class GetTopArtistsResponse(
    val name: String,
    val about: String,
    val playCount: Long
)