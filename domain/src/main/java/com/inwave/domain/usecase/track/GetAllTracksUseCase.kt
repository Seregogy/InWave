package com.inwave.domain.usecase.track

import com.inwave.domain.entity.Track
import com.inwave.domain.repository.query.TrackQueryRepository

class GetAllTracksUseCase(
    private val repository: TrackQueryRepository
) {
    suspend operator fun invoke(page: Int = 0, limit: Int = 20): Result<List<Track>> {
        if (limit > 200)
            return Result.failure(IllegalArgumentException("limit must be less than 200"))

        return repository.getAllTracks(page, limit)
    }
}