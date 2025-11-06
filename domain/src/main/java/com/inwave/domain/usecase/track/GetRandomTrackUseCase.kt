package com.inwave.domain.usecase.track

import com.inwave.domain.entity.Track
import com.inwave.domain.repository.TrackRepository

class GetRandomTrackUseCase(
    val repository: TrackRepository
) {
    suspend operator fun invoke(): Track? {
        return null
    }
}