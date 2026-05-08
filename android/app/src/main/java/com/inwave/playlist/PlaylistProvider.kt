package com.inwave.playlist

interface PlaylistProvider {
    suspend fun getTracks(): List<String>
    suspend fun getAdditionalTracks(count: Int, remain: Int): List<String>
}