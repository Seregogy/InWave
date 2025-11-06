package com.inwave.domain.usecase.track

import com.inwave.domain.entity.Track
import com.inwave.domain.repository.TrackRepository

class ToggleTrackLikeUseCase(
    val repository: TrackRepository
) {
    suspend operator fun invoke(track: Track): Boolean {
        return false
    }
}