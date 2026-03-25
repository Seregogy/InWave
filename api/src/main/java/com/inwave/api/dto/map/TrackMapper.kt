package com.inwave.api.dto.map

import com.inwave.api.dto.track.FullTrackDto
import com.inwave.api.dto.track.GetRandomTrackResponse
import com.inwave.api.dto.track.TrackAdditionalDataDto
import com.inwave.api.dto.track.TrackArtistDetailsDto
import com.inwave.api.dto.track.TrackArtistDto
import com.inwave.api.dto.track.TrackLyricsDto
import com.inwave.api.dto.track.TrackMetadataDto
import com.inwave.api.dto.track.TrackReleaseInfoDto
import com.inwave.api.dto.track.TrackSummaryDto
import com.inwave.domain.entity.Track

fun Track.toGetRandomTrackResponse(): GetRandomTrackResponse {
    return GetRandomTrackResponse(
        this.id,
        this.name,
        this.statistics?.playCount?.toLong() ?: 0L,
        this.hasLyrics,
        this.lyrics?.syncedText ?: mapOf()
    )
}

fun Track.toFullTrackDto(): FullTrackDto {
    return FullTrackDto(
        id = this.id,
        name = this.name,
        coverArtUrl = this.coverArtUrl,
        audioUrl = this.audioUrl,
        durationMs = this.durationMs,
        isExplicit = this.isExplicit,
        placeInRelease = this.placeInRelease,
        genres = this.genres,
        statistics = this.statistics?.toStatisticsDto(),
        release = this.releaseId?.let { releaseId ->
            TrackReleaseInfoDto(
                id = releaseId,
                name = "",
                coverArtUrl = null,
                releaseDate = null
            )
        },
        artists = this.artists.map { it.toTrackArtistDetailsDto() },
        metadata = this.metadata?.toTrackMetadataDto(),
        lyrics = this.lyrics?.toTrackLyricsDto(),
        additionalData = this.additionalData?.toTrackAdditionalDataDto()
    )
}

fun Track.toTrackSummaryDto(): TrackSummaryDto {
    return TrackSummaryDto(
        id = this.id,
        name = this.name,
        coverArtUrl = this.coverArtUrl,
        audioUrl = this.audioUrl,
        durationMs = this.durationMs,
        isExplicit = this.isExplicit,
        artists = this.artists.map { it.toTrackArtistDto() }
    )
}

fun Track.ArtistOnTrack.toTrackArtistDto(): TrackArtistDto {
    return TrackArtistDto(
        id = this.artist.id,
        name = this.artist.name,
        artistType = when (this.artistType) {
            Track.ArtistType.Primary -> TrackArtistDto.ArtistType.Primary
            Track.ArtistType.Featured -> TrackArtistDto.ArtistType.Featured
            Track.ArtistType.Remixer -> TrackArtistDto.ArtistType.Remixer
        }
    )
}

fun Track.ArtistOnTrack.toTrackArtistDetailsDto(): TrackArtistDetailsDto {
    return TrackArtistDetailsDto(
        id = this.artist.id,
        name = this.artist.name,
        imageUrl = this.artist.imagesUrl.firstOrNull(),
        artistType = when (this.artistType) {
            Track.ArtistType.Primary -> TrackArtistDto.ArtistType.Primary
            Track.ArtistType.Featured -> TrackArtistDto.ArtistType.Featured
            Track.ArtistType.Remixer -> TrackArtistDto.ArtistType.Remixer
        }
    )
}

fun Track.Metadata.toTrackMetadataDto(): TrackMetadataDto {
    return TrackMetadataDto(
        bpm = this.bpm,
        format = this.format,
        bitrate = this.bitrate,
        sampleRate = this.sampleRate
    )
}

fun Track.Lyrics.toTrackLyricsDto(): TrackLyricsDto {
    return TrackLyricsDto(
        plainText = this.plainText,
        syncedText = this.syncedText,
        provider = this.provider
    )
}

fun Track.AdditionalData.toTrackAdditionalDataDto(): TrackAdditionalDataDto {
    return TrackAdditionalDataDto(
        fullTitle = this.fullTitle,
        descriptionPreviewPlainText = this.descriptionPreviewPlainText,
        producers = this.producers,
        writers = this.writers,
        tags = this.tags
    )
}