package com.inwave.domain.repository.query

import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track

interface ReleaseQueryRepository {
    suspend fun getRelease(releaseId: String): Result<Release>
    suspend fun getReleaseTracks(releaseId: String): Result<List<Track>>
}