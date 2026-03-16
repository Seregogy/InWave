package com.inwave.domain.usecase.artist.query

import com.inwave.domain.cache.CacheRepository
import com.inwave.domain.repository.query.ArtistQueryRepository
import com.inwave.domain.entity.Artist


class GetArtistUseCase(
    private val repository: ArtistQueryRepository,
    private val cache: CacheRepository<String, Artist>
) {
    suspend operator fun invoke(artistId: String): Result<Artist> {
        if (artistId.isBlank())
             return Result.failure(IllegalArgumentException("Artist ID cannot be empty"))

        cache.get(artistId)?.let {
            return@invoke Result.success(it)
        }

        return repository.getArtist(artistId).also { result ->
            result.onSuccess { cache.put(artistId to it) }
        }
    }
}