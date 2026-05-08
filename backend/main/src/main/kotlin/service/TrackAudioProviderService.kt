package com.inwave.backend.service

interface TrackAudioProviderService {
    fun provideUrl(trackId: String): String
}