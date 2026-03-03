package com.inwave.backend.map

import com.inwave.backend.route.artist.GetTopArtistsResponse
import com.inwave.domain.entity.Artist

fun Artist.toGetTopArtistsResponse(): GetTopArtistsResponse = GetTopArtistsResponse(
    name = this.name,
    about = this.about ?: "",
    playCount = this.statistics?.playCount?.toLong() ?: 0L
)
