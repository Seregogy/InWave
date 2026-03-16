package com.inwave.domain.usecase.track

import com.inwave.domain.cache.CacheRepository
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.TrackRepository

class GetTracksUseCase(
    private val repository: TrackRepository,
    private val cache: CacheRepository<String, Track>,
) {
    suspend operator fun invoke(ids: List<String>): Result<List<Track>> {
        val cachedTracks = ids.mapNotNull { cache.get(it) }
        val missingTracksInCache = ids - cachedTracks.map { it.id }.toSet()

        if (missingTracksInCache.isEmpty())
            return Result.success(cachedTracks)

        val restoredTracks = repository.getTracks(missingTracksInCache).also {
            it.onFailure {
                return@invoke Result.success(cachedTracks)
            }
        }

        return Result.success(cachedTracks + restoredTracks.getOrThrow())
    }
}