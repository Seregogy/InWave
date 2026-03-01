package com.inwave.backend.data.repository.artist

import com.inwave.backend.data.map.toDomain
import com.inwave.backend.data.repository.catchingTransaction
import com.inwave.backend.db.entity.ArtistEntity
import com.inwave.backend.db.table.ArtistStatisticsTable
import com.inwave.backend.db.table.ArtistTable
import com.inwave.backend.db.table.TrackTable
import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.ArtistRepository
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll

class ArtistRepositoryImpl(
    private val db: Database
) : ArtistRepository {
    override suspend fun getArtist(artistId: String): Result<Artist> = catchingTransaction(db) {
        ArtistEntity.findById(artistId.toInt())!!.toDomain()
    }

    override suspend fun getTopArtists(limit: Int): Result<List<Artist>> = catchingTransaction(db) {
        (ArtistTable innerJoin ArtistStatisticsTable).selectAll()
            .limit(limit)
            .orderBy(ArtistStatisticsTable.playCount to SortOrder.DESC).map {
                ArtistEntity.wrapRow(it).toDomain()
            }
    }

    override suspend fun getArtistReleases(artistId: String): Result<List<Release>> = catchingTransaction(db) {
        ArtistEntity.findById(artistId.toInt())!!.releases.map {
            it.toDomain()
        }
    }

    override suspend fun getArtistTopTracks(
        artistId: String,
        limit: Int
    ): Result<List<Track>> {
        TODO("Not yet implemented")
    }
}