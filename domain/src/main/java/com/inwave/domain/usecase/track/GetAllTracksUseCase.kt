package com.inwave.domain.usecase.track

import com.inwave.domain.entity.Track
import com.inwave.domain.repository.TrackRepository

class GetAllTracksUseCase(
    private val repository: TrackRepository
) {
    suspend operator fun invoke(page: Int = 0, limit: Int = 20): Result<List<Track>> {
        if (limit > 200) {
            return Result.failure(IllegalArgumentException("limit > 200"))
        }

        return repository.getAllTracks(page, limit)
    }
}