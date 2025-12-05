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

    val audioId = this.getLong(audioIdColumn)
    val albumId = this.getLong(albumIdColumn)
    val trackName = this.getString(trackNameColumn)
    val duration = this.getLong(durationColumn)

    Log.d("Mapper", "title: $trackName, id: $audioId albumId: $albumId")

    val audioUri = ContentUris.withAppendedId(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        audioId
    )

    return Track(
        "",
        trackName,
        getAlbumArtUri(audioUri.toString()),
        0,
        duration,
        null,
        0,
        audioUri.toString(),
        null,
        listOf()
    )
}

private fun getAlbumArtUri(audioPath: String): String {
    val item = MediaItem.Builder()
        .setUri(audioPath.toUri())
        .build()
    item.mediaMetadata.artworkData?.let {
        val bitmap = byteArrayToBitmap(it)

        return if (bitmap == null)
            ""
        else
            saveBitmapToFile(
                fileName = audioPath.split('/').last(),
                bitmap = bitmap
            )?.toUri().toString()
    }

    return ""
}

fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
    return try {
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun saveBitmapToFile(
    fileName: String,
    bitmap: Bitmap,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    quality: Int = 90
): File? {
    return try {
        val fileName = when (format) {
            Bitmap.CompressFormat.JPEG -> "$fileName.jpg"
            Bitmap.CompressFormat.PNG -> "$fileName.png"
            Bitmap.CompressFormat.WEBP -> "$fileName.webp"
            else -> "$fileName.jpg"
        }

        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        val imageFile = File(storageDir, fileName)

        FileOutputStream(imageFile).use { out ->
            bitmap.compress(format, quality, out)
            out.flush()
        }

        imageFile
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}