package com.inwave.domain.repository.command

interface LikeRepository {
    suspend fun toggleLikeToTrack(authToken: String, trackId: String): Result<Boolean>
    suspend fun toggleLikeToRelease(authToken: String, releaseId: String): Result<Boolean>
}