package com.inwave.domain.usecase.release

import com.inwave.domain.entity.Release
import com.inwave.domain.repository.ReleaseRepository

class GetArtistAlbumsUseCase(
    private val repository: ReleaseRepository
) {
    suspend operator fun invoke(artistId: String): Result<List<Release>> {
        if (artistId.isBlank())
            return Result.failure(IllegalArgumentException("Artist ID cannot be empty"))
        return repository.getArtistAlbums(artistId)
    }
}