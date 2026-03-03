package com.inwave.domain.usecase.artist.query

import com.inwave.domain.repository.query.ArtistQueryRepository
import com.inwave.domain.entity.Artist

class GetTopArtistsUseCase(
    private val repository: ArtistQueryRepository
) {
    suspend operator fun invoke(limit: Int = 10): Result<List<Artist>> {
        if (limit <= 0) {
            return Result.failure(IllegalArgumentException("Limit must be greater than 0"))
        }
        if (limit > 10) {
            return Result.failure(IllegalArgumentException("Limit cannot exceed 10"))
        }
        return repository.getTopArtists(limit)
    }
}