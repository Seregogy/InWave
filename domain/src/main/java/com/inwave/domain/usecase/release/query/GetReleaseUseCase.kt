package com.inwave.domain.usecase.release.query

import com.inwave.domain.entity.Release
import com.inwave.domain.repository.query.ReleaseQueryRepository

class GetReleaseUseCase(
    private val repository: ReleaseQueryRepository
) {
    suspend operator fun invoke(releaseId: String): Result<Release> {
        if (releaseId.isBlank())
            return Result.failure(IllegalArgumentException("Release ID cannot be empty"))

        return repository.getRelease(releaseId)
    }
}