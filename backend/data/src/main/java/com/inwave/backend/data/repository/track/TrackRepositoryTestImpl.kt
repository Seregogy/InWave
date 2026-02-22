package com.inwave.backend.data.repository.track

import com.inwave.domain.entity.Track.Lyrics
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.TrackRepository
import kotlin.collections.listOf

class TrackRepositoryTestImpl : TrackRepository {
    override suspend fun getTrack(id: String): Result<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun getTracks(ids: List<String>): Result<List<Track>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllTracks(
        page: Int,
        size: Int
    ): Result<List<Track>> {
        TODO("Not yet implemented")
    }

    override suspend fun getRandomTrack(): Result<Track> {
        return Result.success(
            Track(
                id = "",
                name = "",
                coverArtUrl = "",
                placeInRelease = null,
                durationMs = null,
                audioUrl = "",
                isExplicit = false,
                genres = listOf(),
                metadata = null,
                statistics = null,
                hasLyrics = false,
                lyrics = null,
                additionalData = null,
                release = null,
                artists = listOf()
            )
        )
    }

    override suspend fun getRandomTrackId(): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun getTrackLyrics(id: String): Result<Lyrics> {
        TODO("Not yet implemented")
    }

    override suspend fun getTrackWithLyrics(id: String): Result<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun toggleLike(id: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun searchTracks(
        query: String,
        limit: Int
    ): Result<List<Track>> {
        TODO("Not yet implemented")
    }
}