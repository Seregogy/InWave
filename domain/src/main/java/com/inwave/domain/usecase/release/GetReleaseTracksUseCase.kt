package com.inwave.domain.usecase.release

import com.inwave.domain.entity.Track
import com.inwave.domain.repository.query.ReleaseQueryRepository

class GetReleaseTracksUseCase(
    private val repository: ReleaseQueryRepository
) {
    suspend operator fun invoke(releaseId: String): Result<List<Track>> {
        if (releaseId.isBlank())
            return Result.failure(IllegalArgumentException("Release ID cannot be empty"))
        return repository.getReleaseTracks(releaseId)
    }
}