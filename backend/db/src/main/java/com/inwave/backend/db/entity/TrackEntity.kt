package com.inwave.backend.db.entity

import com.inwave.backend.db.table.TrackAdditionalDataTable
import com.inwave.backend.db.table.TrackGenresTable
import com.inwave.backend.db.table.TrackLyricsTable
import com.inwave.backend.db.table.TrackMetadataTable
import com.inwave.backend.db.table.TrackReleasesTable
import com.inwave.backend.db.table.TrackStatisticsTable
import com.inwave.backend.db.table.TrackTable
import org.jetbrains.exposed.v1.core.dao.id.CompositeID
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.CompositeEntity
import org.jetbrains.exposed.v1.dao.CompositeEntityClass
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

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
    var releaseDate by TrackTable.releaseDate
}

class TrackMetadataEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TrackMetadataEntity>(TrackMetadataTable)

    var trackId by TrackMetadataTable.trackId
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

    var trackId by TrackReleasesTable.trackId
    var releaseId by TrackReleasesTable.releaseId
    var positionInRelease by TrackReleasesTable.positionInRelease
    var discNumber by TrackReleasesTable.discNumber
}

class TrackGenreEntity(id: EntityID<CompositeID>) : CompositeEntity(id) {
    companion object : CompositeEntityClass<TrackGenreEntity>(TrackGenresTable)

    var trackId by TrackGenresTable.trackId
    var genreId by TrackGenresTable.genreId
    var weight by TrackGenresTable.weight
}