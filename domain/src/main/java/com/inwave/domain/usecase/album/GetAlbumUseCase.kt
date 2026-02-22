package com.inwave.domain.usecase.album

import com.inwave.domain.entity.Release
import domain.repository.AlbumRepository

class GetAlbumUseCase(
    private val repository: AlbumRepository
) {
    suspend operator fun invoke(albumId: String): Result<Release> {
        if (albumId.isNullOrBlank())
            return Result.failure(IllegalArgumentException("Release ID cannot be empty"))
        return repository.getAlbum(albumId)
    }
}