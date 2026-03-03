package com.inwave.domain.repository.command

import com.inwave.domain.entity.Track

interface TrackCommandRepository {
    suspend fun liked(trackId: String): Result<Boolean>
    suspend fun listened(trackId: String): Result<Unit>

    suspend fun addToPlaylist(trackId: String, playlistId: String): Result<Unit>
    suspend fun removeFromPlaylist(trackId: String, playlistId: String): Result<Unit>

    suspend fun createTrack(track: Track): Result<String>
}