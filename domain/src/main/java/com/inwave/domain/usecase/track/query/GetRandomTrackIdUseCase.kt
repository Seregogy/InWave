package com.inwave.domain.usecase.track.query

import com.inwave.domain.repository.query.TrackQueryRepository

class GetRandomTrackIdUseCase(
    private val repository: TrackQueryRepository
) {
    suspend operator fun invoke(): Result<String> {
        return repository.getRandomTrackId()
    }
}