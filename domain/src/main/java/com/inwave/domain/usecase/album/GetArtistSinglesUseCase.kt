package com.inwave.domain.usecase.album

import com.inwave.domain.entity.Album
import domain.repository.AlbumRepository

class GetArtistSinglesUseCase(
    private val repository: AlbumRepository
) {
    suspend operator fun invoke(artistId: String): Result<List<Album>> {
        if (artistId.isNullOrBlank())
            return Result.failure(IllegalArgumentException("Artist ID cannot be empty"))
        return repository.getArtistSingles(artistId)
    }
}