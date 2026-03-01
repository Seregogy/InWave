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
import org.jetbrains.exposed.v1.jdbc.insert

data class ArtistOnTrack(
    val artist: ArtistEntity,
    val track: TrackEntity,
    val artistOnTrackType: ArtistOnTrackType
)

class ArtistEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<ArtistEntity>(ArtistTable) {
        override fun new(id: Int?, init: ArtistEntity.() -> Unit): ArtistEntity {
            val artistEntity = super.new(id) {
                name = ""
            }

            ArtistStatisticsEntity.new { artist = artistEntity }

            with(artistEntity) {
                init()
            }

            return artistEntity
        }
    }

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

    fun addGenre(genreEntity: GenreEntity): Result<Unit> = runCatching {
        ArtistGenresTable.insert {
            it[artistId] = this@ArtistEntity.id
            it[genreId] = genreEntity.id
        }
    }

    fun addRelease(releaseEntity: ReleaseEntity): Result<Unit> = runCatching {
        ArtistReleasesTable.insert {
            it[artistId] = this@ArtistEntity.id
            it[releaseId] = releaseEntity.id
        }
    }

    fun addTrack(track: TrackEntity) {
        ArtistTracksTable.insert {
            it[artistId] = this@ArtistEntity.id
            it[trackId] = track.id
        }
    }
}

class ArtistStatisticsEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ArtistStatisticsEntity>(ArtistStatisticsTable)

    var artist by ArtistEntity referencedOn ArtistStatisticsTable.artistId

    val playCount by ArtistStatisticsTable.playCount
    val likeCount by ArtistStatisticsTable.likeCount
    val repostCount by ArtistStatisticsTable.repostCount
}

class ArtistTracksEntity(id: EntityID<CompositeID>) : CompositeEntity(id) {
    companion object : CompositeEntityClass<ArtistTracksEntity>(ArtistTracksTable)

    var artist by ArtistEntity referencedOn ArtistTracksTable.artistId
    var track by TrackEntity referencedOn ArtistTracksTable.trackId

    var artistType by ArtistTracksTable.artistType
}