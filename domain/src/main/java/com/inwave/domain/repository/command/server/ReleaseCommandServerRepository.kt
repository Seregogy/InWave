package com.inwave.domain.repository.command.server

import com.inwave.domain.entity.Release
import com.inwave.domain.repository.command.LikeRepository

interface ReleaseCommandServerRepository : LikeRepository {
    suspend fun create(release: Release): Result<String>

    suspend fun addTrack(releaseId: String, trackId: String, indexInRelease: Int): Result<Unit>
    suspend fun editCoverArt(releaseId: String, coverArtUrl: String): Result<Unit>

    suspend fun putAdditionalData(releaseId: String, data: Release.AdditionalData): Result<Unit>
    suspend fun patchAdditionalData(releaseId: String, data: Release.AdditionalData): Result<Unit>

    suspend fun addGenre(releaseId: String, genre: String) : Result<Unit>
    suspend fun removeGenre(releaseId: String, genre: String): Result<Unit>
}