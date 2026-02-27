package com.inwave.backend.di

import com.inwave.domain.usecase.track.GetRandomTrackUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory<GetRandomTrackUseCase> {
        GetRandomTrackUseCase(get())
    }
}