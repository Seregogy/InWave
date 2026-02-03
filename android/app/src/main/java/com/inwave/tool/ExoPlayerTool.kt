package com.inwave.tool

import androidx.media3.common.MediaItem
import androidx.media3.common.Player

val Player.mediaItems: List<MediaItem>
    get() = List(this.mediaItemCount) {
        this.getMediaItemAt(it)
    }
