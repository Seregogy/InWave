package com.inwave.api.dto.release

import com.inwave.api.dto.StatisticsDto
import com.inwave.api.dto.artist.ArtistSummaryDto
import com.inwave.api.dto.serialization.LocalDateSerializer
import com.inwave.api.dto.track.TrackSummaryDto
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class ReleaseToggleLikeResponse(
    val liked: Boolean
)

@Serializable
data class FullReleaseDto(
    val id: String,
    val name: String,
    val coverArtUrl: String? = null,
    @Serializable(with = LocalDateSerializer::class)
    val releaseDate: LocalDate? = null,
    val artists: List<ArtistSummaryDto> = emptyList(),
    val genres: List<String> = emptyList(),
    val statistics: StatisticsDto? = null,
    val label: String? = null,
    val tags: List<String> = emptyList(),
    val tracks: List<TrackSummaryDto> = emptyList()
)

@Serializable
data class ReleaseSummaryDto(
    val id: String,
    val name: String,
    val coverArtUrl: String? = null,
    @Serializable(with = LocalDateSerializer::class)
    val releaseDate: LocalDate? = null,
    val type: String,
    val trackCount: Int,
    val artists: List<ArtistSummaryDto>
)