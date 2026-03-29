package com.inwave.backend.data.repository.artist

import com.inwave.backend.data.map.toDomain
import com.inwave.backend.data.repository.catchingTransaction
import com.inwave.backend.db.entity.ArtistEntity
import com.inwave.backend.db.table.ArtistStatisticsTable
import com.inwave.backend.db.table.ArtistTable
import com.inwave.backend.db.table.ReleaseTable
import com.inwave.backend.db.table.ReleaseType
import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.query.ArtistQueryRepository
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.selectAll

class ArtistQueryRepositoryImpl(
    private val db: Database
) : ArtistQueryRepository {
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

    override suspend fun getArtistSingles(artistId: String): Result<List<Release>> = catchingTransaction(db) {
        ArtistEntity.findById(artistId.toInt())!!.releases.filter {
            it.releaseType == ReleaseType.Single
        }.map { it.toDomain() }
    }

    override suspend fun getArtistReleases(artistId: String): Result<List<Release>> = catchingTransaction(db) {
        ArtistEntity.findById(artistId.toInt())!!.releases
            .map { it.toDomain() }
    }

    //TODO: есть вариант сделать это через join, но это на потом =)
    override suspend fun getArtistLastRelease(artistId: String): Result<Pair<Release, Long>> = catchingTransaction(db) {
        ArtistEntity.findById(artistId.toInt())!!.releases.orderBy(ReleaseTable.releaseDate to SortOrder.ASC).first().let {
            it.toDomain() to it.releaseDate!!.toEpochDays()
        }
    }

    override suspend fun getArtistTopTracks(
        artistId: String,
        limit: Int
    ): Result<List<Track>> {
        TODO("Not yet implemented")
    }
}