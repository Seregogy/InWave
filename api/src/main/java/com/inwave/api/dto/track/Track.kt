package com.inwave.api.dto.track

import com.inwave.api.dto.StatisticsDto
import com.inwave.api.dto.serialization.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class TrackSummaryDto(
    val id: String,
    val name: String,
    val coverArtUrl: String? = null,
    val audioUrl: String? = null,
    val durationMs: Long? = null,
    val isExplicit: Boolean = false,
    val artists: List<TrackArtistDto> = emptyList()
)

@Serializable
data class TrackArtistDto(
    val id: String,
    val name: String,
    val artistType: ArtistType
) {
    @Serializable
    enum class ArtistType {
        Primary, Featured, Remixer
    }
}

@Serializable
data class FullTrackDto(
    val id: String,
    val name: String,
    val coverArtUrl: String? = null,
    val audioUrl: String,
    val durationMs: Long? = null,
    val isExplicit: Boolean = false,
    val placeInRelease: Int? = null,
    val genres: List<String> = emptyList(),
    val statistics: StatisticsDto? = null,
    val release: TrackReleaseInfoDto? = null,
    val artists: List<TrackArtistDetailsDto> = emptyList(),
    val metadata: TrackMetadataDto? = null,
    val lyrics: TrackLyricsDto? = null,
    val additionalData: TrackAdditionalDataDto? = null
)

@Serializable
data class TrackArtistDetailsDto(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val artistType: TrackArtistDto.ArtistType
)

@Serializable
data class TrackReleaseInfoDto(
    val id: String,
    val name: String,
    val coverArtUrl: String? = null,
    @Serializable(with = LocalDateSerializer::class)
    val releaseDate: LocalDate? = null
)

@Serializable
data class TrackMetadataDto(
    val bpm: Int? = null,
    val format: String? = null,
    val bitrate: Int? = null,
    val sampleRate: Int? = null
)

@Serializable
data class TrackLyricsDto(
    val plainText: String? = null,
    val syncedText: Map<Long, String>? = null,
    val provider: String? = null
)

@Serializable
data class TrackAdditionalDataDto(
    val fullTitle: String? = null,
    val descriptionPreviewPlainText: String? = null,
    val producers: List<String> = emptyList(),
    val writers: List<String> = emptyList(),
    val tags: List<String> = emptyList()
)