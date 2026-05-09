package com.inwave.domain.service

interface TrackAudioProviderService {
    fun provideUrl(trackId: String): String
}