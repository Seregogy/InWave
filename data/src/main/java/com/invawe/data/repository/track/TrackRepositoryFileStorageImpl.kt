package com.invawe.data.repository.track

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.inwave.domain.entity.Album
import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Lyrics
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.TrackRepository

class TrackRepositoryFileStorageImpl(
    private val context: Context
) : TrackRepository {
    init {

    }

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
        return try {
            val audioFiles = mutableListOf<Track>()

            context.applicationContext.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null
            )?.use {
                val audioIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val trackNameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
                val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)

                while (it.moveToNext()) {
                    val trackName = it.getString(trackNameColumn)
                    val duration = it.getLong(durationColumn)
                    val audioId = it.getLong(audioIdColumn)

                    try {
                        val path = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE))

                        Log.d("Repository", "$trackName, $path")
                    } catch (ex: Exception) { }


                    val audioUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        audioId
                    )

                    Log.d("Repository", "$trackName, $audioUri")

                    audioFiles.add(
                        Track(
                            "",
                            trackName,
                            "",
                            0,
                            duration,
                            null,
                            0,
                            audioUri.toString(),
                            null
                        )
                    )
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

    override suspend fun getTrackLyrics(id: String): Result<Lyrics> {
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