package com.inwave.playlist

import android.util.Log
import com.inwave.domain.usecase.track.query.GetRandomTrackIdUseCase

class PlaylistProviderImpl(
    val baseTracks: List<String> = listOf(),
    private val getRandomTrackId: GetRandomTrackIdUseCase
) : PlaylistProvider {
    override suspend fun getTracks(): List<String> {
        if (baseTracks.isEmpty()) {
            return getAdditionalTracks(5, 0)
        }
        return baseTracks
    }

    override suspend fun getAdditionalTracks(count: Int, remain: Int): List<String> {
        return List(count) {
            getRandomTrackId().getOrNull() ?: ""
        }.also {
            Log.d("Playlist", it.toString())
        }
    }
}