package com.inwave.api.dto.map

import com.inwave.api.dto.artist.ArtistSummaryDto
import com.inwave.api.dto.track.FullTrackDto
import com.inwave.api.dto.track.TrackAdditionalDataDto
import com.inwave.api.dto.track.TrackArtistDetailsDto
import com.inwave.api.dto.track.TrackArtistDto
import com.inwave.api.dto.track.TrackLyricsDto
import com.inwave.api.dto.track.TrackMetadataDto
import com.inwave.api.dto.track.TrackSummaryDto
import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Track

fun Track.toFullTrackDto(): FullTrackDto {
    val release = this.release?.toFullReleaseDto()
    val coverArtUrl = if (this.coverArtUrl.isNullOrEmpty()) release?.coverArtUrl else this.coverArtUrl

    return FullTrackDto(
        id = this.id,
        name = this.name,
        coverArtUrl = coverArtUrl,
        audioUrl = this.audioUrl,
        durationMs = this.durationMs,
        isExplicit = this.isExplicit,
        placeInRelease = this.placeInRelease,
        genres = this.genres,
        statistics = this.statistics?.toStatisticsDto(),
        release = release,
        artists = this.artists
            .map { it.toTrackArtistDetailsDto() }
            .plus(
                release?.artists?.map {
                    it.toTrackArtistDetailsDto()
                } ?: emptyList()
            )
            .toSet()
            .toList(),
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
        artists = this.artists.map { it.toTrackArtistDto() },
        release = this.release?.toReleaseSummaryDto()
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

fun ArtistSummaryDto.toTrackArtistDetailsDto(): TrackArtistDetailsDto {
    return TrackArtistDetailsDto(
        id = this.id,
        name = this.name,
        imageUrl = this.imageUrl,
        artistType = TrackArtistDto.ArtistType.Primary
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

fun TrackSummaryDto.toDomain(): Track {
    return Track(
        id = id,
        releaseId = null,
        name = name,
        coverArtUrl = coverArtUrl,
        audioUrl = audioUrl ?: "",
        durationMs = durationMs,
        isExplicit = isExplicit,
        placeInRelease = null,
        genres = emptyList(),
        metadata = null,
        statistics = null,
        lyrics = null,
        additionalData = null,
        artists = artists.map { it.toDomain() },
        release = this.release?.toDomain()
    )
}

fun FullTrackDto.toDomain(): Track {
    return Track(
        id = id,
        releaseId = release?.id,
        name = name,
        coverArtUrl = coverArtUrl,
        audioUrl = audioUrl,
        durationMs = durationMs,
        isExplicit = isExplicit,
        placeInRelease = placeInRelease,
        genres = genres,
        metadata = metadata?.let {
            Track.Metadata(
                bpm = it.bpm,
                format = it.format,
                bitrate = it.bitrate,
                sampleRate = it.sampleRate
            )
        },
        statistics = statistics?.toDomain(),
        lyrics = lyrics?.toDomain(),
        additionalData = additionalData?.let {
            Track.AdditionalData(
                fullTitle = it.fullTitle,
                descriptionMarkdown = null,
                descriptionPreviewPlainText = it.descriptionPreviewPlainText,
                videoShotUrl = null,
                producers = it.producers,
                writers = it.writers,
                tags = it.tags,
                credits = emptyMap(),
                recordingLocation = null,
                textLanguage = null
            )
        },
        artists = artists.map { it.toDomain() },
        release = release?.toDomain()
    )
}

fun TrackLyricsDto.toDomain(): Track.Lyrics {
    return Track.Lyrics(
        plainText = plainText,
        syncedText = syncedText,
        provider = provider
    )
}

fun TrackArtistDto.toDomain(): Track.ArtistOnTrack {
    return Track.ArtistOnTrack(
        artist = Artist(
            id = id,
            name = name,
            about = null,
            genres = emptyList(),
            imagesUrl = emptyList(),
            statistics = null,
            releases = emptyList()
        ),
        artistType = when (artistType) {
            TrackArtistDto.ArtistType.Primary -> Track.ArtistType.Primary
            TrackArtistDto.ArtistType.Featured -> Track.ArtistType.Featured
            TrackArtistDto.ArtistType.Remixer -> Track.ArtistType.Remixer
        }
    )
}