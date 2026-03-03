package com.inwave.domain.repository.command

interface ReleaseCommandRepository {
    suspend fun liked(): Result<Boolean>

    suspend fun addTrack(releaseId: String, trackId: String): Result<Unit>
}