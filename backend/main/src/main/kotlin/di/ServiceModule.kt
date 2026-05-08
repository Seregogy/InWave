package com.inwave.backend.di

import com.inwave.backend.service.TrackAudioProviderService
import com.inwave.backend.service.TrackAudioProviderStaticService
import org.koin.dsl.module

val serviceModule = module {
    single<TrackAudioProviderService> {
        TrackAudioProviderStaticService(get())
    }
}