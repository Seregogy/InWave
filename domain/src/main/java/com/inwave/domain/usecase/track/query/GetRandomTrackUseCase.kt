package com.inwave.domain.usecase.track.query

import com.inwave.domain.entity.Track
import com.inwave.domain.repository.query.TrackQueryRepository

class GetRandomTrackUseCase(
    private val repository: TrackQueryRepository
) {
    suspend operator fun invoke(): Result<Track> {
        return repository.getRandomTrack()
    }
}