package com.inwave.domain.service

interface VideoShotProviderService {
    fun provideUrl(trackId: String): String
}
