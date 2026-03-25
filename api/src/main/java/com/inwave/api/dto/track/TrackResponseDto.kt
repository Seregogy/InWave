package com.inwave.api.dto.track

import kotlinx.serialization.Serializable

@Serializable
data class GetRandomTrackResponse(
    val id: String,
    val name: String,
    val playCount: Long,
    val hasLyrics: Boolean,
    val syncedText: Map<Long, String>
)