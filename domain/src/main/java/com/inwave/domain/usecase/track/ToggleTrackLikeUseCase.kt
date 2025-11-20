package com.inwave.domain.usecase.track

import com.inwave.domain.entity.Track
import com.inwave.domain.repository.TrackRepository

class ToggleTrackLikeUseCase(
    private val repository: TrackRepository
) {
    suspend operator fun invoke(track: Track): Result<Boolean> {
        return repository.toggleLike(track.id)
    }
}