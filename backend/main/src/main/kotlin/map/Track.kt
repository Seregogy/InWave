package com.inwave.backend.map

import com.inwave.backend.route.track.GetRandomTrackResponse
import com.inwave.domain.entity.Track

fun Track.toGetRandomTrackResponse(): GetRandomTrackResponse {
    return GetRandomTrackResponse(
        this.id,
        this.name,
        this.coverArtUrl,
        this.audioUrl
    )
}