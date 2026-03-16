package com.inwave.domain.usecase.artist

import com.inwave.domain.repository.ArtistRepository
import com.inwave.domain.entity.Track

class GetArtistTopTracksUseCase(
    private val repository: ArtistRepository
) {
    suspend operator fun invoke(artistId: String, limit: Int = 9): Result<List<Track>> {
        if (artistId.isBlank()) {
            return Result.failure(IllegalArgumentException("Artist ID cannot be empty"))
        }
        if (limit <= 0) {
            return Result.failure(IllegalArgumentException("Limit must be greater than 0"))
        }
        if (limit > 50) {
            return Result.failure(IllegalArgumentException("Limit cannot exceed 50"))
        }
        return repository.getArtistTopTracks(artistId, limit)
    }
}