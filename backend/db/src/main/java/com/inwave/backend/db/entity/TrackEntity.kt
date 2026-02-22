package com.inwave.backend.db.entity

import com.inwave.backend.db.table.ArtistTracksTable
import com.inwave.backend.db.table.TrackAdditionalDataTable
import com.inwave.backend.db.table.TrackGenreTable
import com.inwave.backend.db.table.TrackLyricsTable
import com.inwave.backend.db.table.TrackMetadataTable
import com.inwave.backend.db.table.TrackReleasesTable
import com.inwave.backend.db.table.TrackStatisticsTable
import com.inwave.backend.db.table.TrackTable
import org.jetbrains.exposed.v1.core.dao.id.CompositeID
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.CompositeEntity
import org.jetbrains.exposed.v1.dao.CompositeEntityClass
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import kotlin.collections.List

data class TrackOnRelease(
    val track: TrackEntity,
    val release: ReleaseEntity,
    val positionInRelease: Int?,
    val discNumber: Int?
)

data class GenreOnTrack(
    val genre: GenreEntity,
    val track: TrackEntity,
    val weight: Float?
)

class TrackEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TrackEntity>(TrackTable)

    var name by TrackTable.name
    var durationMs by TrackTable.durationMs
    var isExplicit by TrackTable.isExplicit
    var hasLyrics by TrackTable.hasLyrics

    var coverArtUrl by TrackTable.coverArtUrl
    var previewStartMs by TrackTable.previewStartMs
    var previewDurationMs by TrackTable.previewDurationMs

    var createdAt by TrackTable.createdAt
    var updatedAt by TrackTable.updatedAt

    fun fetchGenres(): List<GenreOnTrack> =
        TrackGenreEntity.find { TrackGenreTable.trackId eq id }.map {
            GenreOnTrack(it.genre, it.track, it.weight)
        }

    fun fetchMetadata(): TrackMetadataEntity? =
        TrackMetadataEntity.find { TrackMetadataTable.trackId eq id }.firstOrNull()

    fun fetchStatistics(): TrackStatisticsEntity? =
        TrackStatisticsEntity.find { TrackStatisticsTable.trackId eq id }.firstOrNull()

    fun fetchLyrics(): TrackLyricsEntity? =
        TrackLyricsEntity.find { TrackLyricsTable.trackId eq id }.firstOrNull()

    fun fetchAdditionalData(): TrackAdditionalDataEntity? =
        TrackAdditionalDataEntity.find { TrackAdditionalDataTable.trackId eq id }.firstOrNull()

    fun fetchArtists(): List<ArtistOnTrack> =
        ArtistTracksEntity.find { ArtistTracksTable.trackId eq id }.map {
            ArtistOnTrack(it.artist, it.track, it.artistType)
        }
}

class TrackMetadataEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TrackMetadataEntity>(TrackMetadataTable)

    var track by TrackEntity referencedOn TrackMetadataTable.trackId
    var bpm by TrackMetadataTable.bpm
    var fileFormat by TrackMetadataTable.fileFormat
    var bitrate by TrackMetadataTable.bitrate
    var sampleRate by TrackMetadataTable.sampleRate
}

class TrackStatisticsEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TrackStatisticsEntity>(TrackStatisticsTable)

    var trackId by TrackStatisticsTable.trackId

    var playCount by TrackStatisticsTable.playCount
    var likeCount by TrackStatisticsTable.likeCount
    var repostCount by TrackStatisticsTable.repostCount
}

class TrackLyricsEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TrackLyricsEntity>(TrackLyricsTable)

    var trackId by TrackLyricsTable.trackId

    var plainText by TrackLyricsTable.plainText
    var syncedText by TrackLyricsTable.syncedText
    var provider by TrackLyricsTable.provider
}

class TrackAdditionalDataEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TrackAdditionalDataEntity>(TrackAdditionalDataTable)

    var trackId by TrackAdditionalDataTable.trackId

    var fullTitle by TrackAdditionalDataTable.fullTitle
    var descriptionMd by TrackAdditionalDataTable.descriptionMd
    var descriptionPreviewPlain by TrackAdditionalDataTable.descriptionPreviewPlain
    var videoShotUrl by TrackAdditionalDataTable.videoShotUrl
    var producers by TrackAdditionalDataTable.producers
    var writers by TrackAdditionalDataTable.writers
    var tags by TrackAdditionalDataTable.tags
    var recordingLocation by TrackAdditionalDataTable.recordingLocation
    var textLanguage by TrackAdditionalDataTable.textLanguage

    var credits by TrackAdditionalDataTable.credits
}

class TrackReleaseEntity(id: EntityID<CompositeID>) : CompositeEntity(id) {
    companion object : CompositeEntityClass<TrackReleaseEntity>(TrackReleasesTable)

    val track by TrackEntity referencedOn TrackReleasesTable.trackId
    val release by ReleaseEntity referencedOn TrackReleasesTable.releaseId

    var positionInRelease by TrackReleasesTable.positionInRelease
    var discNumber by TrackReleasesTable.discNumber
}

class TrackGenreEntity(id: EntityID<CompositeID>) : CompositeEntity(id) {
    companion object : CompositeEntityClass<TrackGenreEntity>(TrackGenreTable)

    val track by TrackEntity referencedOn TrackGenreTable.trackId
    val genre by GenreEntity referencedOn TrackGenreTable.genreId
    var weight by TrackGenreTable.weight
}