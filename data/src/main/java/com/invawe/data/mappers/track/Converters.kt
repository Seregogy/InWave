 package com.invawe.data.mappers.track

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.inwave.domain.entity.Album
import com.inwave.domain.entity.Artist
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

 //Дополнительная функция для преобразования списка треков
 fun List<Track>.toMediaItems(): List<MediaItem> = this.map { it.toMediaItem() }

fun Cursor.toDomainTrack(): Track {
    val audioIdColumn = this.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
    val trackNameColumn = this.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
    val durationColumn = this.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)

    val id = getLong(getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
    val trackName = this.getString(trackNameColumn)
    val duration = this.getLong(durationColumn)
    val audioId = this.getLong(audioIdColumn)

    val artistColumn = getColumnIndex(MediaStore.Audio.Media.ARTIST)
    val albumIdColumn = getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
    val albumColumn = getColumnIndex(MediaStore.Audio.Media.ALBUM)
    val trackNumberColumn = getColumnIndex(MediaStore.Audio.Media.TRACK)

    val artist = if (artistColumn != -1) getString(artistColumn) else null
    val albumId = if (albumIdColumn != -1) getLong(albumIdColumn) else null
    val albumName = if (albumColumn != -1) getString(albumColumn) else null
    val trackNumber = if (trackNumberColumn != -1) getInt(trackNumberColumn) else 0

    val genreColumn = getColumnIndex(MediaStore.Audio.Media.GENRE)
    val yearColumn = getColumnIndex(MediaStore.Audio.Media.YEAR)
    val composerColumn = getColumnIndex(MediaStore.Audio.Media.COMPOSER)

    val genre = if (genreColumn != -1) getString(genreColumn) ?: "" else ""
    val year = if (yearColumn != -1) getInt(yearColumn) else 0
    val composer = if (composerColumn != -1) getString(composerColumn) ?: "" else ""

    val audioUri = ContentUris.withAppendedId(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        audioId
    )

    // URI для обложки альбома
    val albumArtColumn = getColumnIndex(MediaStore.Audio.Albums.getContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()).toString())
    val albumArtUri = if (albumArtColumn != -1) {
        val artPath = getString(albumArtColumn)
        if (!artPath.isNullOrEmpty()) {
            "file://$artPath"
        } else {
            ""
        }
    } else {
        ""
    }

    // Создаем объект Album
    val album = if (albumId != null && albumName != null) {
        Album(
            id = albumId.toString(),
            name = albumName,
            imageUrl = albumArtUri,
            artists = if (!artist.isNullOrEmpty()) {
                listOf(Artist(id = "", name = artist, imageUrl = ""))
            } else {
                emptyList()
            },
            likes = 0,
            listening = 0,
            releaseDate = if (year > 0) {
                // Преобразуем год в timestamp (1 января указанного года)
                java.time.LocalDate.of(year, 1, 1)
                    .atStartOfDay(java.time.ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            } else {
                0L
            },
            genre = genre,
            label = composer // Используем композитора как лейбл, или оставляем пустым
        )
    } else {
        null
    }


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
        listOf()
    )
}