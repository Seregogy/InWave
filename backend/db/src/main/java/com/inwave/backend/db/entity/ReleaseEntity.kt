package com.inwave.backend.db.entity

import com.inwave.backend.db.table.ArtistReleasesTable
import com.inwave.backend.db.table.ReleaseAdditionalDataTable
import com.inwave.backend.db.table.ReleaseGenreTable
import com.inwave.backend.db.table.ReleaseStatisticsTable
import com.inwave.backend.db.table.ReleaseTable
import com.inwave.backend.db.table.TrackReleasesTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

class ReleaseEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ReleaseEntity>(ReleaseTable)

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
        TrackReleaseEntity.find { TrackReleasesTable.releaseId eq id }.map {
            TrackOnRelease(it.track, it.release, it.positionInRelease, it.discNumber)
        }
}

class ReleaseStatisticsEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ReleaseStatisticsEntity>(ReleaseStatisticsTable)

    var release by ReleaseEntity referencedOn ReleaseStatisticsTable.releaseId
    var playCount by ReleaseStatisticsTable.playCount
    var likeCount by ReleaseStatisticsTable.likeCount
    var repostCount by ReleaseStatisticsTable.repostCount

}

class ReleaseAdditionalDataEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ReleaseAdditionalDataEntity>(ReleaseAdditionalDataTable)

    val release by ReleaseEntity referencedOn ReleaseAdditionalDataTable.releaseId

    var fullTitle by ReleaseAdditionalDataTable.fullTitle
    var descriptionMd by ReleaseAdditionalDataTable.descriptionMd
    var descriptionPreviewPlain by ReleaseAdditionalDataTable.descriptionPreviewPlain
    var label by ReleaseAdditionalDataTable.label
    var tags by ReleaseAdditionalDataTable.tags
    var credits by ReleaseAdditionalDataTable.credits
}