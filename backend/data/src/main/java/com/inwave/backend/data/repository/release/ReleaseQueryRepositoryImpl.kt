package com.inwave.backend.data.repository.release

import com.inwave.backend.data.map.toDomain
import com.inwave.backend.data.repository.catchingTransaction
import com.inwave.backend.db.entity.ArtistEntity
import com.inwave.backend.db.entity.ReleaseEntity
import com.inwave.backend.db.table.ReleaseTable
import com.inwave.backend.db.table.ReleaseType
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.query.ReleaseQueryRepository
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.jdbc.Database
import kotlin.collections.map

class ReleaseQueryRepositoryImpl(
    private val db: Database
) : ReleaseQueryRepository {
    override suspend fun getRelease(releaseId: String): Result<Release> = catchingTransaction(db) {
        ReleaseEntity.findById(releaseId.toInt())!!.toDomain()
    }

    override suspend fun getReleaseTracks(releaseId: String): Result<List<Track>> = catchingTransaction(db) {
        ReleaseEntity.findById(releaseId.toInt())!!.fetchTracks().map {
            it.track.toDomain(
                releaseId = releaseId
            )
        }
    }

    override suspend fun getArtistAlbums(artistId: String): Result<List<Release>> = catchingTransaction(db) {
        ArtistEntity.findById(artistId.toInt())!!.releases.filter {
            it.releaseType == ReleaseType.Album
        }.map { it.toDomain() }
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
}