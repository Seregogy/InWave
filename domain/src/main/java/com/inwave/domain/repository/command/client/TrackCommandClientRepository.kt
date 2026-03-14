package com.inwave.domain.repository.command.client

import com.inwave.domain.repository.command.LikeRepository

interface TrackCommandClientRepository : LikeRepository {
    suspend fun listened(trackId: String): Result<Unit>

    suspend fun addToPlaylist(trackId: String, playlistId: String): Result<Unit>
    suspend fun removeFromPlaylist(trackId: String, playlistId: String): Result<Unit>
}