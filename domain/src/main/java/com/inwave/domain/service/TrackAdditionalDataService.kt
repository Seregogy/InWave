package com.inwave.domain.service

import com.inwave.domain.entity.Track

interface TrackAdditionalDataService {
    suspend fun fetchTrackAdditionalData(
        track: Track
    ): Result<Track.AdditionalData>
}