package com.inwave.domain.usecase.album

import com.inwave.domain.entity.Album
import domain.repository.AlbumRepository

class GetAlbumUseCase(
    private val repository: AlbumRepository
) {
    suspend operator fun invoke(albumId: String): Result<Album> {
        if (albumId.isNullOrBlank())
            return Result.failure(IllegalArgumentException("Album ID cannot be empty"))
        return repository.getAlbum(albumId)
    }
}