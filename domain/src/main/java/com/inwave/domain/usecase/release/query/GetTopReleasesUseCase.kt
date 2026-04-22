package com.inwave.domain.usecase.release.query

import com.inwave.domain.entity.Release
import com.inwave.domain.repository.query.ReleaseQueryRepository

class GetTopReleasesUseCase(
    private val repository: ReleaseQueryRepository
) {
    suspend operator fun invoke(limit: Int = 10): Result<List<Release>> {
        if (limit <= 0) {
            return Result.failure(IllegalArgumentException("Limit must be greater than 0"))
        }
        if (limit > 10) {
            return Result.failure(IllegalArgumentException("Limit cannot exceed 10"))
        }
        return repository.getTopReleases(limit)
    }
}