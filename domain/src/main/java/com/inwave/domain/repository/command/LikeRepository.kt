package com.inwave.domain.repository.command

interface LikeRepository {
    suspend fun toggleLike(userId: String, resourceId: String): Result<Boolean>
}