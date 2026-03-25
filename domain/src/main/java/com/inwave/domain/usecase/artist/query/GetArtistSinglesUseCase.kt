package com.inwave.domain.usecase.artist.query

import com.inwave.domain.entity.Release
import com.inwave.domain.repository.query.ArtistQueryRepository

class GetArtistSinglesUseCase(
    private val repository: ArtistQueryRepository
) {
    suspend operator fun invoke(artistId: String): Result<List<Release>> {
        if (artistId.isBlank())
            return Result.failure(IllegalArgumentException("Artist ID cannot be empty"))

        return repository.getArtistSingles(artistId)
    }
}