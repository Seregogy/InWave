package com.inwave.backend.data.repository.release

import com.inwave.backend.data.map.toDomain
import com.inwave.backend.data.repository.catchingTransaction
import com.inwave.backend.db.entity.ReleaseEntity
import com.inwave.backend.db.table.ReleaseStatisticsTable
import com.inwave.backend.db.table.ReleaseTable
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.query.ReleaseQueryRepository
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.selectAll

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

    override suspend fun getTopReleases(limit: Int): Result<List<Release>> = catchingTransaction(db) {
        (ReleaseTable innerJoin ReleaseStatisticsTable)
            .selectAll()
            .orderBy(ReleaseStatisticsTable.playCount, SortOrder.DESC)
            .take(limit)
            .map {
                ReleaseEntity.wrapRow(it)
                    .toDomain()
            }
    }
}