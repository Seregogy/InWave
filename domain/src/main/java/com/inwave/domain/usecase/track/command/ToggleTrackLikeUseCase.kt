package com.inwave.domain.usecase.track.command

import com.inwave.domain.repository.command.client.TrackCommandClientRepository

class ToggleTrackLikeUseCase(
    private val trackCommandClientRepository: TrackCommandClientRepository
) {
    suspend operator fun invoke(userId: String, trackId: String): Result<Boolean> {
        return trackCommandClientRepository.toggleLikeToTrack(userId, trackId)
    }
}