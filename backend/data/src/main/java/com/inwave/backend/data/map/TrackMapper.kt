package com.inwave.backend.data.map

import com.inwave.backend.db.entity.ArtistOnTrack
import com.inwave.backend.db.entity.ReleaseEntity
import com.inwave.backend.db.entity.TrackAdditionalDataEntity
import com.inwave.backend.db.entity.TrackEntity
import com.inwave.backend.db.entity.TrackLyricsEntity
import com.inwave.backend.db.entity.TrackMetadataEntity
import com.inwave.backend.db.entity.TrackStatisticsEntity
import com.inwave.backend.db.table.ArtistOnTrackType
import com.inwave.domain.entity.Statistics
import com.inwave.domain.entity.Track
import org.jetbrains.exposed.v1.core.Transaction

context(_: Transaction)
fun TrackEntity.toDomain(
    audioUrl: String = "",
    releaseId: String? = null
): Track {
    val positionInRelease = releaseId?.let {
        ReleaseEntity.findById(it.toInt())?.fetchTracks()?.first { trackOnRelease ->
            trackOnRelease.track.id == this.id
        }?.positionInRelease
    }

    val lyrics = this.fetchLyrics()

    return Track(
        id = id.value.toString(),
        releaseId = releaseId,
        name = name,
        coverArtUrl = coverArtUrl,
        audioUrl = audioUrl,
        durationMs = durationMs,
        isExplicit = isExplicit,
        placeInRelease = positionInRelease,
        genres = fetchGenres().map { it.genre.name },
        metadata = fetchMetadata()?.toDomain(),
        statistics = fetchStatistics()?.toDomain(),
        hasLyrics = (lyrics != null),
        lyrics = lyrics?.toDomain(),
        additionalData = fetchAdditionalData()?.toDomain(),
        artists = fetchArtists().map { it.toDomain() }
    )
}

fun TrackMetadataEntity.toDomain(): Track.Metadata =
    Track.Metadata(
        bpm = bpm,
        format = fileFormat,
        bitrate = bitrate,
        sampleRate = sampleRate
    )


fun TrackStatisticsEntity.toDomain(): Statistics =
    Statistics(
        playCount = playCount.toInt(),
        likeCount = likeCount,
        repostCount = repostCount
    )

fun TrackLyricsEntity.toDomain(): Track.Lyrics =
    Track.Lyrics(
        plainText = plainText,
        syncedText = syncedText,
        provider = provider
    )

fun TrackAdditionalDataEntity.toDomain() =
    Track.AdditionalData(
        fullTitle = fullTitle,
        descriptionMarkdown = descriptionMd,
        descriptionPreviewPlainText = descriptionPreviewPlain,
        videoShotUrl = videoShotUrl,
        producers = producers ?: listOf(),
        writers = writers ?: listOf(),
        tags = tags ?: listOf(),
        credits = credits,
        recordingLocation = recordingLocation,
        textLanguage = textLanguage
    )

private val artistTypeConverter = mapOf(
    ArtistOnTrackType.Primary to Track.ArtistType.Primary,
    ArtistOnTrackType.Featured to Track.ArtistType.Featured,
    ArtistOnTrackType.Remixer to Track.ArtistType.Remixer
)

context(_: Transaction)
fun ArtistOnTrack.toDomain(): Track.ArtistOnTrack =
    Track.ArtistOnTrack(
        artist = artist.toDomain(),
        artistType = artistTypeConverter[artistOnTrackType] ?: Track.ArtistType.Primary
    )