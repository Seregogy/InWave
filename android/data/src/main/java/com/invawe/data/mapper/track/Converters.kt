package com.invawe.data.mapper.track

import android.content.ContentUris
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.inwave.domain.entity.Track
import androidx.core.net.toUri
import com.inwave.domain.entity.Artist

fun Track.toMediaItem(): MediaItem = MediaItem.Builder()
        .setMediaId(id)
        .setUri(audioUrl)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(name)
                .setDisplayTitle(name)
                .setArtist(artists.joinToString(", ") { it.artist.name })
                .setArtworkUri(coverArtUrl?.toUri())
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
    val urls = listOf(
        "https://images.genius.com/aa7b86debf8b362bad3018cb881cfdc1.1000x1000x1.png",
        "https://images.genius.com/9abf18ca07ce3513522ea5b1ec286d79.1000x1000x1.png",
        "https://images.genius.com/6715e9ef15be0bb90f8371b5e68ada39.1000x1000x1.png",
        "https://images.genius.com/acc7c50e803663c05226570b1d73f338.1000x1000x1.png",
    )

    return Track(
        id = "$audioId",
        releaseId = "",
        name = trackName,
        coverArtUrl = "",
        audioUrl = audioUri.toString(),
        durationMs = duration,
        isExplicit = false,
        placeInRelease = 0,
        genres = listOf(),
        metadata = null,
        statistics = null,
        hasLyrics = false,
        lyrics = null,
        additionalData = null,
        artists = listOf(
            Track.ArtistOnTrack(
                artist = Artist(
                    id = "",
                    name = artists,
                    about = null,
                    genres = listOf(),
                    imagesUrl = listOf(),
                    statistics = null,
                    releases = listOf()
                ),
                artistType = Track.ArtistType.Primary
            )
        )
    )
}