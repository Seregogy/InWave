package com.inwave.domain.usecase.artist

import com.inwave.domain.repository.ArtistRepository
import com.inwave.domain.entity.Artist

class GetTopArtistsUseCase(
    private val repository: ArtistRepository
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