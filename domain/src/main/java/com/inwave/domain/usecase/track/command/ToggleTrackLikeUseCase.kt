package com.inwave.domain.usecase.track.command

import com.inwave.domain.entity.Track
import com.inwave.domain.repository.command.TrackCommandRepository

class ToggleTrackLikeUseCase(
    private val repository: TrackCommandRepository
) {
    suspend operator fun invoke(track: Track): Result<Boolean> {
        return repository.liked(track.id)
    }
}