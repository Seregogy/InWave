package com.inwave.domain.usecase.album

import com.inwave.domain.entity.Track
import domain.repository.AlbumRepository

class GetAlbumTracksUseCase(
    private val repository: AlbumRepository
) {
    suspend operator fun invoke(albumId: String): Result<List<Track>> {
        if (albumId.isNullOrBlank())
            return Result.failure(IllegalArgumentException("Album ID cannot be empty"))
        return repository.getAlbumTracks(albumId)
    }
}