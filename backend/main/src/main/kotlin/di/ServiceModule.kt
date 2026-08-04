package com.inwave.backend.di

import com.inwave.backend.service.provider.TrackAudioProviderStaticService
import com.inwave.backend.service.provider.VideoShotProviderStaticService
import com.inwave.backend.service.track.additionaldata.TrackAdditionalDataRabbitMqService
import com.inwave.domain.service.TrackAdditionalDataService
import com.inwave.domain.service.TrackAudioProviderService
import com.inwave.domain.service.VideoShotProviderService
import org.koin.core.qualifier.named
import org.koin.dsl.module

val serviceModule = module {
    single<TrackAudioProviderService> {
        TrackAudioProviderStaticService(get())
    }

    single<VideoShotProviderService> {
        VideoShotProviderStaticService(get())
    }

    single<TrackAdditionalDataService>(named("rabbitmq")) {
        TrackAdditionalDataRabbitMqService(get(), get())
    }
}