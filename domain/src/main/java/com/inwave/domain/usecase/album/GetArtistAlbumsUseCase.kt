package com.inwave.domain.usecase.album

import com.inwave.domain.entity.Release
import domain.repository.AlbumRepository

class GetArtistAlbumsUseCase(
    private val repository: AlbumRepository
) {
    suspend operator fun invoke(artistId: String): Result<List<Release>> {
        if (artistId.isNullOrBlank())
            return Result.failure(IllegalArgumentException("Artist ID cannot be empty"))
        return repository.getArtistAlbums(artistId)
    }
}