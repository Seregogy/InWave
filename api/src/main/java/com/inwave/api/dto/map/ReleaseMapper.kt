package com.inwave.api.dto.map

import com.inwave.api.dto.release.FullReleaseDto
import com.inwave.api.dto.release.ReleaseSummaryDto
import com.inwave.domain.entity.Release

fun Release.toFullReleaseDto(): FullReleaseDto {
    return FullReleaseDto(
        id = this.id,
        name = this.name,
        coverArtUrl = this.coverArtUrl,
        releaseDate = this.releaseDate,
        artists = this.artists.map { it.toArtistSummaryDto() },
        genres = this.genres,
        statistics = this.statistics?.toStatisticsDto(),
        label = this.additionalData?.label,
        tags = this.additionalData?.tags ?: emptyList(),
        tracks = this.tracks.map { it.toTrackSummaryDto() }
    )
}

fun Release.toReleaseSummaryDto(): ReleaseSummaryDto {
    return ReleaseSummaryDto(
        id = this.id,
        name = this.name,
        coverArtUrl = this.coverArtUrl,
        releaseDate = this.releaseDate,
        type = this.determineReleaseType(),
        trackCount = this.tracks.size,
        artists = this.artists.map { it.toArtistSummaryDto() }
    )
}

fun FullReleaseDto.toDomain(): Release {
    return Release(
        id = id,
        name = name,
        coverArtUrl = coverArtUrl,
        releaseDate = releaseDate,
        tracks = tracks.map { it.toDomain() },
        artists = artists.map { it.toDomain() },
        genres = genres,
        statistics = statistics?.toDomain(),
        additionalData = Release.AdditionalData(
            fullTitle = name,
            descriptionMarkdown = null,
            descriptionPreviewPlainText = null,
            label = label,
            tags = tags,
            credits = emptyMap()
        )
    )
}

fun ReleaseSummaryDto.toDomain(): Release {
    return Release(
        id = id,
        name = name,
        coverArtUrl = coverArtUrl,
        releaseDate = releaseDate,
        tracks = emptyList(),
        artists = emptyList(),
        genres = emptyList(),
        statistics = null,
        additionalData = null
    )
}