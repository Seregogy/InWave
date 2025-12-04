package com.invawe.data.mappers.track

import android.net.Uri
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