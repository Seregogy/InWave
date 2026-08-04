package com.inwave.domain.usecase.track.command.server

import com.inwave.domain.entity.Track
import com.inwave.domain.repository.command.server.TrackCommandServerRepository
import com.inwave.domain.service.TrackAdditionalDataService
import com.inwave.domain.usecase.track.query.GetTrackUseCase

class FetchAdditionalDataUseCase(
    private val getTrackUseCase: GetTrackUseCase,
    private val additionalDataService: TrackAdditionalDataService,
    private val trackCommandServerRepository: TrackCommandServerRepository
) {
    suspend operator fun invoke(id: String): Result<Track> {
        val track = getTrackUseCase(id).getOrNull() ?: return Result.failure(
            Exception("Track with id $id was not found")
        )


        val additionalData = track.additionalData ?:
            additionalDataService.fetchTrackAdditionalData(track).onFailure { exception ->
                return Result.failure(exception)
            }.getOrNull() ?: return Result.failure(
                Exception("Additional data was not found for track with id $id")
            )

        trackCommandServerRepository.patchAdditionalData(id, additionalData).onFailure { exception ->
            return Result.failure(exception)
        }.getOrNull() ?: return Result.failure(
            Exception("Failure to put additional data for track with id $id")
        )

        return Result.success(track.copy(additionalData = additionalData))
    }
}