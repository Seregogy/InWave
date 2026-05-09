package com.inwave.backend.di

import com.inwave.backend.service.provider.TrackAudioProviderStaticService
import com.inwave.domain.service.TrackAudioProviderService
import org.koin.dsl.module

val serviceModule = module {
    single<TrackAudioProviderService> {
        TrackAudioProviderStaticService(get())
    }
}