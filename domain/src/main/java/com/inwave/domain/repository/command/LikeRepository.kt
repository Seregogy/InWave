package com.inwave.domain.repository.command

interface LikeRepository {
    suspend fun toggleLikeToTrack(userId: String, trackId: String): Result<Boolean>
    suspend fun toggleLikeToRelease(userId: String, releaseId: String): Result<Boolean>
}