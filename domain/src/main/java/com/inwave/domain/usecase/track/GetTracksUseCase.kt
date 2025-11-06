package com.inwave.domain.usecase.track

import com.inwave.domain.entity.Track
import com.inwave.domain.repository.TrackRepository

class GetTracksUseCase(
    val repository: TrackRepository
) {
    suspend operator fun invoke(): List<Track>? {
        return null
    }
}