package com.inwave.domain.usecase.track

import com.inwave.domain.entity.Track
import com.inwave.domain.repository.TrackRepository

class GetRandomTrackUseCase(
    private val repository: TrackRepository
) {
    suspend operator fun invoke(): Result<Track> {
        return repository.getRandomTrack()
    }
}