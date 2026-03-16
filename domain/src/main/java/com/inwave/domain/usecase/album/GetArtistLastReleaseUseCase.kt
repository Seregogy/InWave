package com.inwave.domain.usecase.album

import com.inwave.domain.entity.Album
import domain.repository.AlbumRepository

class GetArtistLastReleaseUseCase(
    private val repository: AlbumRepository
) {
    suspend operator fun invoke(artistId: String): Result<Pair<Album, Long>> {
        if (artistId.isNullOrBlank())
            return Result.failure(IllegalArgumentException("Artist ID cannot be empty"))
        return repository.getArtistLastRelease(artistId)
    }
}