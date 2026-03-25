package com.inwave.api.dto.map

import com.inwave.api.dto.artist.ArtistSummaryDto
import com.inwave.api.dto.artist.ArtistTrackSummaryDto
import com.inwave.api.dto.artist.FullArtistDto
import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Track

fun Artist.toFullArtistDto(): FullArtistDto {
    return FullArtistDto(
        id = this.id,
        name = this.name,
        about = this.about,
        avatarUrls = this.imagesUrl,
        genres = this.genres,
        statistics = this.statistics?.toStatisticsDto(),
        releases = this.releases.map { it.toReleaseSummaryDto() }
    )
}

fun Artist.toArtistSummaryDto(): ArtistSummaryDto {
    return ArtistSummaryDto(
        id = this.id,
        name = this.name,
        imageUrl = this.imagesUrl.firstOrNull(),
        genres = this.genres,
        statistics = this.statistics?.toStatisticsDto()
    )
}

fun Artist.toArtistTrackSummaryDto(tracks: List<Track>): List<ArtistTrackSummaryDto> {
    return tracks.map { track ->
        ArtistTrackSummaryDto(
            id = track.id,
            name = track.name,
            coverArtUrl = track.coverArtUrl,
            durationMs = track.durationMs,
            statistics = track.statistics?.toStatisticsDto()
        )
    }
}