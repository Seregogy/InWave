package com.inwave.api.dto.track

import kotlinx.serialization.Serializable

@Serializable
data class AdditionalTrackData(
    val fullTitle: String? = null,
    val shortDescription: String? = null,
    val description: String? = null,
    val producers: List<String> = listOf(),
    val writers: List<String> = listOf(),
    val tags: List<String> = listOf(),
    val duration: Long? = null,
    val credits: Map<String, List<String>> = mapOf(),
    val language: String? = null,
    val recordingLocation: String? = null,
    val releaseDate: Long? = null,
    val imageUrl: String? = null
)

@Serializable
data class FetchAdditionalTrackDataRequest(
    val id: String,
    val name: String,
    val albumName: String,
    val artists: String
)