package com.inwave.backend.db.entity

import com.inwave.backend.db.table.ArtistOnTrackType
import com.inwave.backend.db.table.ArtistTable
import com.inwave.backend.db.table.ArtistTrackTable
import com.inwave.backend.db.table.ReleaseTable
import com.inwave.backend.db.table.ReleaseTrackTable
import com.inwave.backend.db.table.TrackAdditionalDataTable
import com.inwave.backend.db.table.TrackGenreTable
import com.inwave.backend.db.table.TrackLyricsTable
import com.inwave.backend.db.table.TrackMetadataTable
import com.inwave.backend.db.table.TrackStatisticsTable
import com.inwave.backend.db.table.TrackTable
import org.jetbrains.exposed.v1.core.dao.id.CompositeID
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.minus
import org.jetbrains.exposed.v1.core.plus
import org.jetbrains.exposed.v1.dao.CompositeEntity
import org.jetbrains.exposed.v1.dao.CompositeEntityClass
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update

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
    companion object : IntEntityClass<TrackEntity>(TrackTable) {
        override fun new(id: Int?, init: TrackEntity.() -> Unit): TrackEntity {
            val track = super.new(id) {
                name = ""
            }

            TrackMetadataEntity.new { this.track = track }
            TrackStatisticsEntity.new { this.track = track }
            TrackLyricsEntity.new { this.track = track }
            TrackAdditionalDataEntity.new {
                this.track = track
                credits = mapOf()
            }

            with(track) {
                init()
            }

            return track
        }
    }

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

    fun fetchRelease(): TrackOnRelease =
        (ReleaseTrackTable innerJoin ReleaseTable)
            .select(ReleaseTrackTable.columns + ReleaseTable.columns)
            .where { ReleaseTrackTable.trackId eq id }
            .first()
            .let { row ->
                TrackOnRelease(
                    track = this,
                    release = ReleaseEntity.findById(row[ReleaseTrackTable.releaseId].value)!!,
                    positionInRelease = row[ReleaseTrackTable.positionInRelease],
                    discNumber = row[ReleaseTrackTable.discNumber]
                )
            }

    fun fetchArtists(): List<ArtistOnTrack> =
        (ArtistTrackTable innerJoin ArtistTable innerJoin TrackTable)
            .select(ArtistTable.columns + TrackTable.columns + ArtistTrackTable.artistType)
            .where { ArtistTrackTable.trackId eq id }
            .map { row ->
                ArtistOnTrack(
                    artist = ArtistEntity.wrapRow(row),
                    track = wrapRow(row),
                    artistOnTrackType = row[ArtistTrackTable.artistType]
                )
            }

    fun addGenre(genre: GenreEntity, weight: Float? = null): Result<Unit> = runCatching {
        TrackGenreTable.insert {
            it[trackId] = this@TrackEntity.id
            it[TrackGenreTable.genreId] = genre.id
            it[TrackGenreTable.weight] = weight
        }
    }

    fun addArtist(artistEntity: ArtistEntity, artistType: ArtistOnTrackType): Result<Unit> = runCatching {
        ArtistTrackTable.insert {
            it[artistId] = artistEntity.id
            it[trackId] = this@TrackEntity.id
            it[ArtistTrackTable.artistType] = artistType
        }
    }

    fun updateMetadata(metadataEntity: TrackMetadataEntity): Result<Unit> = runCatching {
        fetchMetadata()?.apply {
            metadataEntity.bpm?.let { bpm = it }
            metadataEntity.fileFormat?.let { fileFormat = it }
            metadataEntity.bitrate?.let { bitrate = it }
            metadataEntity.sampleRate?.let { sampleRate = it }
        } ?: metadataEntity.apply {
            track = this@TrackEntity
        }
    }

    fun updateLyrics(lyricsEntity: TrackLyricsEntity): Result<Unit> = runCatching {
        fetchLyrics()?.apply {
            lyricsEntity.syncedText?.let {
                syncedText = it
            }
            lyricsEntity.plainText?.let {
                plainText = it
            }
            lyricsEntity.provider?.let { provider = it }

            hasLyrics = (syncedText != null || plainText != null)
        } ?: lyricsEntity.apply {
            track = this@TrackEntity
        }
    }

    fun updateAdditionalData(additionalDataEntity: TrackAdditionalDataEntity): Result<Unit> = runCatching {
        fetchAdditionalData()?.apply {
            additionalDataEntity.fullTitle?.let { fullTitle = it }
            additionalDataEntity.descriptionMd?.let { descriptionMd = it }
            additionalDataEntity.descriptionPreviewPlain?.let { descriptionPreviewPlain = it }
            additionalDataEntity.videoShotUrl?.let { videoShotUrl = it }
            additionalDataEntity.producers?.let { producers = it }
            additionalDataEntity.writers?.let { writers = it }
            additionalDataEntity.tags?.let { tags = it }
            additionalDataEntity.recordingLocation?.let { recordingLocation = it }
            additionalDataEntity.textLanguage?.let { textLanguage = it }

            if (additionalDataEntity.credits.isNotEmpty()) {
                credits = additionalDataEntity.credits
            }
        } ?: additionalDataEntity.apply {
            track = this@TrackEntity
        }
    }

    fun increasePlayCount(): Result<Unit> = runCatching {
        TrackStatisticsTable.update({ TrackStatisticsTable.trackId eq this.id }) {
            it.update(playCount, playCount + 1)
        }
    }

    fun increaseRepostCount(): Result<Unit> = runCatching {
        TrackStatisticsTable.update({ TrackStatisticsTable.trackId eq this.id }) {
            it.update(repostCount, repostCount + 1)
        }
    }

    fun increaseLikeCount(): Result<Unit> = runCatching {
        TrackStatisticsTable.update({ TrackStatisticsTable.trackId eq this.id }) {
            it.update(likeCount, likeCount + 1)
        }
    }

    fun decreaseLikeCount(): Result<Unit> = runCatching {
        TrackStatisticsTable.update({ TrackStatisticsTable.trackId eq this.id }) {
            it.update(likeCount, likeCount - 1)
        }
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

    var track by TrackEntity referencedOn TrackStatisticsTable.trackId

    val playCount by TrackStatisticsTable.playCount
    val likeCount by TrackStatisticsTable.likeCount
    val repostCount by TrackStatisticsTable.repostCount
}

class TrackLyricsEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TrackLyricsEntity>(TrackLyricsTable)

    var track by TrackEntity referencedOn TrackLyricsTable.trackId

    var plainText by TrackLyricsTable.plainText
    var syncedText by TrackLyricsTable.syncedText
    var provider by TrackLyricsTable.provider
}

class TrackAdditionalDataEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TrackAdditionalDataEntity>(TrackAdditionalDataTable)

    var track by TrackEntity referencedOn TrackAdditionalDataTable.trackId

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

class TrackGenreEntity(id: EntityID<CompositeID>) : CompositeEntity(id) {
    companion object : CompositeEntityClass<TrackGenreEntity>(TrackGenreTable)

    var track by TrackEntity referencedOn TrackGenreTable.trackId
    var genre by GenreEntity referencedOn TrackGenreTable.genreId
    var weight by TrackGenreTable.weight
}