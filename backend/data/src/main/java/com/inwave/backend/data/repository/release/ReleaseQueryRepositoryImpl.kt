package com.inwave.backend.data.repository.release

import com.inwave.backend.data.map.toDomain
import com.inwave.backend.data.repository.catchingTransaction
import com.inwave.backend.db.entity.ReleaseEntity
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.query.ReleaseQueryRepository
import org.jetbrains.exposed.v1.jdbc.Database

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
}