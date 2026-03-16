package com.inwave.domain.usecase.track

import com.inwave.domain.cache.CacheRepository
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.TrackRepository

class GetTrackUseCase(
    private val repository: TrackRepository,
    private val cache: CacheRepository<String, Track>
) {
    suspend operator fun invoke(id: String): Result<Track> {
        cache.get(id)?.let {
            return@invoke Result.success(it)
        }

        return repository.getTrack(id).also { result ->
            result.onSuccess { cache.put(id to it) }
        }
    }
}