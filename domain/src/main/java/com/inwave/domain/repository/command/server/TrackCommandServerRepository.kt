package com.inwave.domain.repository.command.server

import com.inwave.domain.entity.Track
import com.inwave.domain.repository.command.LikeRepository

interface TrackCommandServerRepository : LikeRepository {
    suspend fun create(track: Track): Result<String>

    suspend fun editCoverArt(trackId: String, coverArtUrl: String): Result<Unit>
    suspend fun editPreviewTiming(previewStartMs: Int, previewDurationMs: Int)

    suspend fun putMetadata(trackId: String, metadata: Track.Metadata)
    suspend fun patchMetadata(trackId: String, metadata: Track.Metadata)

    suspend fun putAdditionalData(trackId: String, data: Track.AdditionalData): Result<Unit>
    suspend fun patchAdditionalData(trackId: String, data: Track.AdditionalData): Result<Unit>

    suspend fun putLyrics(trackId: String, lyrics: Track.Lyrics): Result<Unit>
    suspend fun patchLyrics(trackId: String, lyrics: Track.Lyrics): Result<Unit>

    suspend fun setIsExplicit(trackId: String, value: Boolean): Result<Unit>

    suspend fun addGenre(trackId: String, genre: String, weight: Float) : Result<Unit>
    suspend fun removeGenre(trackId: String, genre: String): Result<Unit>
}