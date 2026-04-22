package com.inwave.backend.db.entity

import com.inwave.backend.db.table.ArtistGenreTable
import com.inwave.backend.db.table.ArtistOnTrackType
import com.inwave.backend.db.table.ArtistReleaseTable
import com.inwave.backend.db.table.ArtistStatisticsTable
import com.inwave.backend.db.table.ArtistTable
import com.inwave.backend.db.table.ArtistTrackTable
import com.inwave.backend.db.table.ArtistTrackTable.artistType
import com.inwave.backend.db.table.TrackTable
import org.jetbrains.exposed.v1.core.dao.id.CompositeID
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.CompositeEntity
import org.jetbrains.exposed.v1.dao.CompositeEntityClass
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select

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

    val genres by GenreEntity via ArtistGenreTable
    val releases by ReleaseEntity via ArtistReleaseTable

    fun fetchStatistics(): ArtistStatisticsEntity? =
        ArtistStatisticsEntity.find { ArtistStatisticsTable.artistId eq id }.firstOrNull()

    fun fetchTracks(): List<ArtistOnTrack> =
        (TrackTable innerJoin ArtistTrackTable).select(
            TrackTable.columns + artistType
        ).where { ArtistTrackTable.artistId eq id }.map {
            ArtistOnTrack(
                artist = this,
                track = TrackEntity.wrapRow(it),
                artistOnTrackType = it[artistType]
            )
        }

    fun addGenre(genreEntity: GenreEntity): Result<Unit> = runCatching {
        ArtistGenreTable.insert {
            it[artistId] = this@ArtistEntity.id
            it[genreId] = genreEntity.id
        }
    }

    fun addRelease(releaseEntity: ReleaseEntity): Result<Unit> = runCatching {
        ArtistReleaseTable.insert {
            it[artistId] = this@ArtistEntity.id
            it[releaseId] = releaseEntity.id
        }
    }

    fun addTrack(
        track: TrackEntity,
        artistOnTrackType: ArtistOnTrackType = ArtistOnTrackType.Primary
    ) {
        ArtistTrackTable.insert {
            it[artistId] = this@ArtistEntity.id
            it[trackId] = track.id
            it[artistType] = artistOnTrackType
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
    companion object : CompositeEntityClass<ArtistTracksEntity>(ArtistTrackTable)

    var artist by ArtistEntity referencedOn ArtistTrackTable.artistId
    var track by TrackEntity referencedOn ArtistTrackTable.trackId

    var artistType by ArtistTrackTable.artistType
}