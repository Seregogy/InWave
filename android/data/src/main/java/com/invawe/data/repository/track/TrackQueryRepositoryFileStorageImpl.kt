package com.invawe.data.repository.track

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.invawe.data.mapper.track.toDomainTrack
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.query.TrackQueryRepository

class TrackQueryRepositoryFileStorageImpl(
    private val context: Context
) : TrackQueryRepository {
    override suspend fun getTrack(id: String): Result<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun getTracks(ids: List<String>): Result<List<Track>> {
        return getAllTracks(0, Int.MAX_VALUE).onSuccess {
            it.forEach { track ->
                if (track.id == ids.first()) {
                    Log.d("LocalDataReposImpl", "Found track ${track.id}")
                    return@getTracks Result.success(listOf(track))
                }
            }

            return Result.failure(Resources.NotFoundException())
        }
    }

    override suspend fun getAllTracks(
        page: Int,
        size: Int
    ): Result<List<Track>> {
        return try {
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

            val audioFiles = mutableListOf<Track>()
            context.applicationContext.contentResolver.query(
                collection,
                null, null, null
            )?.use {
                require(it.count > 0) {
                    "Ничего не найдено"
                }

                while (it.moveToNext()) {
                    audioFiles.add(it.toDomainTrack())
                }
            }

            Result.success(audioFiles.chunked(size)[page])
        } catch (exc: Exception) {
            Result.failure(exc)
        }
    }

    override suspend fun getRandomTrack(): Result<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun getRandomTrackId(): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun getTrackLyrics(id: String): Result<Track.Lyrics> {
        TODO("Not yet implemented")
    }

    override suspend fun getTrackWithLyrics(id: String): Result<Track> {
        TODO("Not yet implemented")
    }

    override suspend fun searchTracks(
        query: String,
        limit: Int
    ): Result<List<Track>> {
        TODO("Not yet implemented")
    }
}