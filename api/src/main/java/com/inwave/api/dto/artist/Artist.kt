package com.inwave.api.dto.artist

import com.inwave.api.dto.StatisticsDto
import com.inwave.api.dto.release.ReleaseSummaryDto
import kotlinx.serialization.Serializable

@Serializable
data class ArtistSummaryDto(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val genres: List<String> = emptyList(),
    val statistics: StatisticsDto? = null
)

@Serializable
data class FullArtistDto(
    val id: String,
    val name: String,
    val about: String? = null,
    val avatarUrls: List<String> = emptyList(),
    val genres: List<String> = emptyList(),
    val statistics: StatisticsDto? = null,
    val releases: List<ReleaseSummaryDto> = emptyList()
)

@Serializable
data class ArtistTrackSummaryDto(
    val id: String,
    val name: String,
    val coverArtUrl: String? = null,
    val durationMs: Long? = null,
    val statistics: StatisticsDto? = null
)