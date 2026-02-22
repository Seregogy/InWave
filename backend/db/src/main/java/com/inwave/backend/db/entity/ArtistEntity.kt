package com.inwave.backend.db.entity

import com.inwave.backend.db.table.ArtistGenresTable
import com.inwave.backend.db.table.ArtistOnTrackType
import com.inwave.backend.db.table.ArtistReleasesTable
import com.inwave.backend.db.table.ArtistStatisticsTable
import com.inwave.backend.db.table.ArtistTable
import com.inwave.backend.db.table.ArtistTracksTable
import org.jetbrains.exposed.v1.core.dao.id.CompositeID
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.CompositeEntity
import org.jetbrains.exposed.v1.dao.CompositeEntityClass
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

data class ArtistOnTrack(
    val artist: ArtistEntity,
    val track: TrackEntity,
    val artistOnTrackType: ArtistOnTrackType
)

class ArtistEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<ArtistEntity>(ArtistTable)

    var name by ArtistTable.name
    var avatarUrls by ArtistTable.avatarUrls
    var about by ArtistTable.about

    val genres by GenreEntity via ArtistGenresTable
    val releases by ReleaseEntity via ArtistReleasesTable

    fun fetchStatistics(): ArtistStatisticsEntity? =
        ArtistStatisticsEntity.find { ArtistStatisticsTable.artistId eq id }.firstOrNull()

    fun fetchTracks(): List<ArtistOnTrack> =
        ArtistTracksEntity.find { ArtistTracksTable.artistId eq id }.map {
            ArtistOnTrack(it.artist, it.track, it.artistType)
        }
}

class ArtistStatisticsEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ArtistStatisticsEntity>(ArtistStatisticsTable)

    val artist by ArtistEntity referencedOn ArtistStatisticsTable.artistId

    var playCount by ArtistStatisticsTable.playCount
    var likeCount by ArtistStatisticsTable.likeCount
    var repostCount by ArtistStatisticsTable.repostCount
}

class ArtistTracksEntity(id: EntityID<CompositeID>) : CompositeEntity(id) {
    companion object : CompositeEntityClass<ArtistTracksEntity>(ArtistTracksTable)

    val artist by ArtistEntity referencedOn ArtistTracksTable.artistId
    val track by TrackEntity referencedOn ArtistTracksTable.trackId

    var artistType by ArtistTracksTable.artistType
}