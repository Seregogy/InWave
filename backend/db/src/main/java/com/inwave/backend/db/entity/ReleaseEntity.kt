package com.inwave.backend.db.entity

import com.inwave.backend.db.table.ArtistReleasesTable
import com.inwave.backend.db.table.ReleaseAdditionalDataTable
import com.inwave.backend.db.table.ReleaseGenreTable
import com.inwave.backend.db.table.ReleaseStatisticsTable
import com.inwave.backend.db.table.ReleaseTable
import com.inwave.backend.db.table.ReleaseType
import com.inwave.backend.db.table.TrackReleaseTable
import com.inwave.backend.db.table.TrackStatisticsTable
import com.inwave.backend.db.table.TrackStatisticsTable.likeCount
import com.inwave.backend.db.table.TrackStatisticsTable.playCount
import com.inwave.backend.db.table.TrackStatisticsTable.repostCount
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.minus
import org.jetbrains.exposed.v1.core.plus
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.jdbc.SizedCollection
import org.jetbrains.exposed.v1.jdbc.SizedIterable
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.update

class ReleaseEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ReleaseEntity>(ReleaseTable) {
        override fun new(id: Int?, init: ReleaseEntity.() -> Unit): ReleaseEntity {
            val release = super.new(id) {
                name = ""
            }

            ReleaseStatisticsEntity.new { this.release = release }
            ReleaseAdditionalDataEntity.new {
                this.release = release
                credits = mapOf()
            }

            with(release) {
                init()
            }

            return release
        }
    }

    var name by ReleaseTable.name
    var releaseType by ReleaseTable.releaseType
    var coverArtUrl by ReleaseTable.coverArtUrl

    var totalTracks by ReleaseTable.totalTracks
    var totalDurationMs by ReleaseTable.totalDurationMs

    var createdAt by ReleaseTable.createdAt
    var updatedAt by ReleaseTable.updatedAt
    var releaseDate by ReleaseTable.releaseDate

    val genres by GenreEntity via ReleaseGenreTable
    val artists by ArtistEntity via ArtistReleasesTable

    fun fetchStatistics(): ReleaseStatisticsEntity? =
        ReleaseStatisticsEntity.find { ReleaseStatisticsTable.releaseId eq id }.firstOrNull()

    fun fetchAdditionalData(): ReleaseAdditionalDataEntity? =
        ReleaseAdditionalDataEntity.find { ReleaseAdditionalDataTable.releaseId eq id }.firstOrNull()

    fun fetchTracks(): List<TrackOnRelease> =
        TrackReleaseEntity.find { TrackReleaseTable.releaseId eq id }.map {
            TrackOnRelease(it.track, it.release, it.positionInRelease, it.discNumber)
        }

    fun addGenre(genreEntity: GenreEntity): Result<Unit> = runCatching {
        ReleaseGenreTable.insert {
            it[genreId] = genreEntity.id
            it[releaseId] = this@ReleaseEntity.id
        }
    }

    fun addTrack(
        trackEntity: TrackEntity,
        positionInRelease: Int? = null,
        diskNumber: Int? = null
    ): Result<Unit> = runCatching {
        TrackReleaseTable.insert {
            it[releaseId] = this@ReleaseEntity.id
            it[trackId] = trackEntity.id
            it[TrackReleaseTable.positionInRelease] = positionInRelease ?: (totalTracks - 1)
            it[TrackReleaseTable.discNumber] = diskNumber ?: 0
        }

        totalTracks++
        totalDurationMs += trackEntity.durationMs

        releaseType = defineReleaseType()
    }

    fun addArtist(artistEntity: ArtistEntity): Result<Unit> = runCatching {
        ArtistReleasesTable.insert {
            it[artistId] = artistEntity.id
            it[releaseId] = this@ReleaseEntity.id
        }
    }

    fun updateAdditionalData(additionalDataEntity: ReleaseAdditionalDataEntity) {
        fetchAdditionalData()?.apply {
            additionalDataEntity.fullTitle?.let { fullTitle = it }
            additionalDataEntity.descriptionMd?.let { descriptionMd = it }
            additionalDataEntity.descriptionPreviewPlain?.let { descriptionPreviewPlain = it }
            additionalDataEntity.label?.let { label = it }
            additionalDataEntity.tags?.let { tags = it }
            additionalDataEntity.credits.let { credits = it }
        }
    }

    fun increasePlayCount(): Result<Unit> = runCatching {
        ReleaseStatisticsTable.update({ ReleaseStatisticsTable.releaseId eq this.id }) {
            it.update(playCount, playCount + 1)
        }
    }

    fun increaseRepostCount(): Result<Unit> = runCatching {
        ReleaseStatisticsTable.update({ ReleaseStatisticsTable.releaseId eq this.id }) {
            it.update(repostCount, repostCount + 1)
        }
    }

    fun increaseLikeCount(): Result<Unit> = runCatching {
        ReleaseStatisticsTable.update({ ReleaseStatisticsTable.releaseId eq this.id }) {
            it.update(likeCount, likeCount + 1)
        }
    }

    fun decreaseLikeCount(): Result<Unit> = runCatching {
        ReleaseStatisticsTable.update({ ReleaseStatisticsTable.releaseId eq this.id }) {
            it.update(likeCount, likeCount - 1)
        }
    }

    private fun defineReleaseType(): ReleaseType = when(totalTracks) {
        1 -> ReleaseType.Single
        in 2..5 -> ReleaseType.EP
        else -> ReleaseType.Album
    }
}

class ReleaseStatisticsEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ReleaseStatisticsEntity>(ReleaseStatisticsTable)

    var release by ReleaseEntity referencedOn ReleaseStatisticsTable.releaseId

    val playCount by ReleaseStatisticsTable.playCount
    val likeCount by ReleaseStatisticsTable.likeCount
    val repostCount by ReleaseStatisticsTable.repostCount
}

class ReleaseAdditionalDataEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ReleaseAdditionalDataEntity>(ReleaseAdditionalDataTable)

    var release by ReleaseEntity referencedOn ReleaseAdditionalDataTable.releaseId

    var fullTitle by ReleaseAdditionalDataTable.fullTitle
    var descriptionMd by ReleaseAdditionalDataTable.descriptionMd
    var descriptionPreviewPlain by ReleaseAdditionalDataTable.descriptionPreviewPlain
    var label by ReleaseAdditionalDataTable.label
    var tags by ReleaseAdditionalDataTable.tags
    var credits by ReleaseAdditionalDataTable.credits
}