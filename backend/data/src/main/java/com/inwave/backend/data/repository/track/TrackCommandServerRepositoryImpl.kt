package com.inwave.backend.data.repository.track

import com.inwave.backend.data.repository.catchingTransaction
import com.inwave.backend.db.entity.TrackAdditionalDataEntity
import com.inwave.backend.db.entity.TrackEntity
import com.inwave.backend.db.migration.AdditionalTrackDataEntity
import com.inwave.backend.db.table.TrackAdditionalDataTable
import com.inwave.backend.db.table.TrackTable
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.command.server.TrackCommandServerRepository
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.upsert
import org.jetbrains.exposed.v1.jdbc.upsertReturning

class TrackCommandServerRepositoryImpl(
    private val db: Database
) : TrackCommandServerRepository {
    override suspend fun create(track: Track): Result<String> = catchingTransaction(db) {
        TODO()
    }

    override suspend fun editCoverArt(
        trackId: String,
        coverArtUrl: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun editPreviewTiming(
        previewStartMs: Int,
        previewDurationMs: Int
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun putMetadata(
        trackId: String,
        metadata: Track.Metadata
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun patchMetadata(
        trackId: String,
        metadata: Track.Metadata
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun putAdditionalData(
        trackId: String,
        data: Track.AdditionalData
    ): Result<Unit> = catchingTransaction(db) {
        val id = trackId.toIntOrNull() ?: throw Exception("Invalid ID format")

        TrackAdditionalDataTable.upsert(TrackAdditionalDataTable.trackId) {
            it[TrackAdditionalDataTable.trackId] = id

            it[TrackAdditionalDataTable.fullTitle] = data.fullTitle
            it[TrackAdditionalDataTable.descriptionMd] = data.descriptionMarkdown
            it[TrackAdditionalDataTable.descriptionPreviewPlain] = data.descriptionPreviewPlainText
            it[TrackAdditionalDataTable.videoShotUrl] = data.videoShotUrl
            it[TrackAdditionalDataTable.producers] = data.producers
            it[TrackAdditionalDataTable.writers] = data.writers
            it[TrackAdditionalDataTable.tags] = data.tags
            it[TrackAdditionalDataTable.recordingLocation] = data.recordingLocation
            it[TrackAdditionalDataTable.textLanguage] = data.textLanguage
            it[TrackAdditionalDataTable.credits] = data.credits
        }
    }

    override suspend fun patchAdditionalData(
        trackId: String,
        data: Track.AdditionalData
    ): Result<Unit> {
        return putAdditionalData(trackId, data)
    }

    override suspend fun putLyrics(
        trackId: String,
        lyrics: Track.Lyrics
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun patchLyrics(
        trackId: String,
        lyrics: Track.Lyrics
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun setIsExplicit(
        trackId: String,
        value: Boolean
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun addGenre(
        trackId: String,
        genre: String,
        weight: Float
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun removeGenre(
        trackId: String,
        genre: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun toggleLikeToTrack(
        authToken: String,
        trackId: String
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun toggleLikeToRelease(
        authToken: String,
        releaseId: String
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

}