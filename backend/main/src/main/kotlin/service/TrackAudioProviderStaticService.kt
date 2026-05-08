package com.inwave.backend.service

import io.github.cdimascio.dotenv.Dotenv

class TrackAudioProviderStaticService(
    private val dotenv: Dotenv
) : TrackAudioProviderService {
    override fun provideUrl(trackId: String): String {
        return "${dotenv["DOMAIN_URL"]}/audio/$trackId.mp3"
    }
}