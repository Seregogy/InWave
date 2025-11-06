package com.inwave.domain.repository

import com.inwave.domain.entity.Lyrics
import com.inwave.domain.entity.Track

interface TrackRepository {
    suspend fun getTrack(id: String): Result<Track>
    suspend fun getTracks(ids: List<String>): Result<List<Track>>
    suspend fun getRandomTrack(): Result<Track>
    suspend fun getRandomTrackId(): Result<String>
    suspend fun getTrackLyrics(id: String): Result<Lyrics>
    suspend fun toggleLike(id: String): Result<Boolean>
    suspend fun searchTracks(query: String, limit: Int = 10): Result<List<Track>>
}