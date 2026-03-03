package com.inwave.domain.usecase.track

import com.inwave.domain.cache.CacheRepository
import com.inwave.domain.entity.Track.Lyrics
import com.inwave.domain.repository.query.TrackQueryRepository

class GetTrackLyricsUseCase(
    private val repository: TrackQueryRepository,
    private val cache: CacheRepository<String, Lyrics>
) {
    suspend operator fun invoke(id: String): Result<Lyrics> {
        cache.get(id)?.let {
            return@invoke Result.success(it)
        }

        return repository.getTrackLyrics(id).also { result ->
            result.onSuccess { cache.put(id to it) }
        }
    }
}