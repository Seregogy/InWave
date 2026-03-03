package com.inwave.domain.usecase.release

import com.inwave.domain.entity.Release
import com.inwave.domain.repository.query.ReleaseQueryRepository

class GetArtistSinglesUseCase(
    private val repository: ReleaseQueryRepository
) {
    suspend operator fun invoke(artistId: String): Result<List<Release>> {
        if (artistId.isBlank())
            return Result.failure(IllegalArgumentException("Artist ID cannot be empty"))
        return repository.getArtistSingles(artistId)
    }
}