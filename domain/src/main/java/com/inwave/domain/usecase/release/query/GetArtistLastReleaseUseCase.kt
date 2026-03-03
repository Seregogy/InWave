package com.inwave.domain.usecase.release.query

import com.inwave.domain.entity.Release
import com.inwave.domain.repository.query.ReleaseQueryRepository

class GetArtistLastReleaseUseCase(
    private val repository: ReleaseQueryRepository
) {
    suspend operator fun invoke(artistId: String): Result<Pair<Release, Long>> {
        if (artistId.isBlank())
            return Result.failure(IllegalArgumentException("Artist ID cannot be empty"))
        return repository.getArtistLastRelease(artistId)
    }
}