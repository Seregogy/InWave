package com.invawe.data.mapper.track

import android.content.ContentUris
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.inwave.domain.entity.Track
import androidx.core.net.toUri
import com.inwave.domain.entity.Artist
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Track.toMediaItem(): MediaItem = MediaItem.Builder()
        .setMediaId(id)
        .setUri(audioUrl)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(name)
                .setDisplayTitle(name)
                .setAlbumTitle(album?.name ?: "unknown")
                .setArtist(album?.artists?.joinToString(", ") { it.name } ?: "unknown")
                .setArtworkUri(imageUrl.toUri())
                .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                .build()
        )
        .build()

fun Cursor.toDomainTrack(): Track {
    val audioIdColumn = this.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
    val albumIdColumn = this.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
    val trackNameColumn = this.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
    val durationColumn = this.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
    val artistsColumn = this.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)

    val audioId = this.getLong(audioIdColumn)
    val albumId = this.getLong(albumIdColumn)
    val trackName = this.getString(trackNameColumn)
    val duration = this.getLong(durationColumn)
    val artists = this.getString(artistsColumn)

    Log.d("Mapper", "title: $trackName, id: $audioId albumId: $albumId")

    val audioUri = ContentUris.withAppendedId(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        audioId
    )

    return Track(
        "",
        trackName,
        "",
        0,
        duration,
        null,
        0,
        audioUri.toString(),
        null,
        listOf(
            Artist(
                id = "",
                name = artists
            )
        )
    )
}