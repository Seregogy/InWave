package com.inwave.backend.service.provider

import com.inwave.domain.service.VideoShotProviderService
import io.github.cdimascio.dotenv.Dotenv

class VideoShotProviderStaticService(
    private val dotenv: Dotenv
) : VideoShotProviderService {
    override fun provideUrl(trackId: String): String {
        return "${dotenv["DOMAIN_URL"]}/video/$trackId.mp4"
    }
}