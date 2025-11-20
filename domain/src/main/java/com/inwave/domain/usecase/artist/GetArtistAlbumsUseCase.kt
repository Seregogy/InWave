package com.inwave.domain.usecase.artist

import com.inwave.domain.repository.ArtistRepository
import com.inwave.domain.entity.Album

class GetArtistAlbumsUseCase(
    private val repository: ArtistRepository
) {
    suspend operator fun invoke(artistId: String): Result<List<Album>> {
        if (artistId.isBlank()) {
            return Result.failure(IllegalArgumentException("Artist ID cannot be empty"))
        }
        return repository.getArtistAlbums(artistId)
    }
}