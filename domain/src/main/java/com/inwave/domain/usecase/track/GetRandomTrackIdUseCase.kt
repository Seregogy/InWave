package com.inwave.domain.usecase.track

import com.inwave.domain.repository.TrackRepository

class GetRandomTrackIdUseCase(
    val repository: TrackRepository
) {
    suspend operator fun invoke(): String? {
        return null
    }
}