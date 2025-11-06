package com.inwave.domain.usecase.track

import com.inwave.domain.entity.Track
import com.inwave.domain.repository.TrackRepository

class GetTrackUseCase(
    val repository: TrackRepository,
) {
    suspend operator fun invoke(id: String): Track? {
        return null
    }
}