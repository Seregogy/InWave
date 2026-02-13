package com.inwave.backend.data.map

import com.inwave.domain.entity.Track

data class TrackDAO(
    val track_id: String,
    val name: String,
    val audio_url: String
)

fun TrackDAO.toDomain(): Track {
    return Track(
        this.track_id,
        this.name,
        "",
        -1,
        -1,
        null,
        -1,
        this.audio_url,
        null,
        listOf()
    )
}