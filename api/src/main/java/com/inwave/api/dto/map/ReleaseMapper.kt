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
        trackCount = this.tracks.size
    )
}