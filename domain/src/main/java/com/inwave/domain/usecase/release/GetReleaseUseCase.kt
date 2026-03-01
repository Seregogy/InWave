package com.inwave.domain.usecase.release

import com.inwave.domain.entity.Release
import com.inwave.domain.repository.ReleaseRepository

class GetReleaseUseCase(
    private val repository: ReleaseRepository
) {
    suspend operator fun invoke(releaseId: String): Result<Release> {
        if (releaseId.isBlank())
            return Result.failure(IllegalArgumentException("Release ID cannot be empty"))

        return repository.getRelease(releaseId)
    }
}