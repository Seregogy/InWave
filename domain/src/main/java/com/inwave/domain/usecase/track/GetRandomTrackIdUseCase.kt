package com.inwave.domain.usecase.track

import com.inwave.domain.repository.TrackRepository

class GetRandomTrackIdUseCase(
    private val repository: TrackRepository
) {
    suspend operator fun invoke(): Result<String> {
        return repository.getRandomTrackId()
    }
}