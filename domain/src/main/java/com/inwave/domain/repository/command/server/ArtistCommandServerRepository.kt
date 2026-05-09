package com.inwave.domain.repository.command.server

import com.inwave.domain.entity.Artist
import com.inwave.domain.repository.command.LikeRepository

interface ArtistCommandServerRepository : LikeRepository {
    suspend fun create(artist: Artist): Result<String>

    suspend fun addRelease(artistId: String, releaseId: String): Result<Unit>

    suspend fun addAvatar(artistId: String, avatarUrl: String): Result<Unit>
    suspend fun removeAvatar(artistId: String, avatarUrl: String): Result<Unit>

    suspend fun editAbout(artistId: String, about: String): Result<Unit>

    suspend fun addGenre(artistId: String, genre: String) : Result<Unit>
    suspend fun removeGenre(artistId: String, genre: String): Result<Unit>
}