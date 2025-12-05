package com.invawe.data.mappers.track

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.inwave.domain.entity.Track

fun Track.toMediaItem(): MediaItem = MediaItem.Builder()
        .setMediaId(id)
        .setUri(audioUrl)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(name)
                .setDisplayTitle(name)
                .setAlbumTitle(album?.name ?: "unknown")
                .setArtist(album?.artists?.joinToString(", ") { it.name } ?: "unknown")
                .setArtworkUri(Uri.parse(imageUrl))
                .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                .build()
        )
        .build()

fun Cursor.toDomainTrack(): Track {
    val audioIdColumn = this.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
    val trackNameColumn = this.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
    val durationColumn = this.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)

    val trackName = this.getString(trackNameColumn)
    val duration = this.getLong(durationColumn)
    val audioId = this.getLong(audioIdColumn)

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
        null
    )
}