package com.inwave.domain.repository.command

import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track

interface ArtistCommandRepository {
    suspend fun liked(): Result<Boolean>

    suspend fun addRelease(artistId: String, releaseId: String): Result<Unit>
    suspend fun updateArtist(artistId: String, artist: Artist): Result<Unit>

    suspend fun createArtist(artist: Artist): Result<String>
}