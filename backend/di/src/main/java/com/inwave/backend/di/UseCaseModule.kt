package com.inwave.backend.di

import com.inwave.domain.usecase.artist.query.GetTopArtistsUseCase
import com.inwave.domain.usecase.track.query.GetRandomTrackUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single<GetRandomTrackUseCase> {
        GetRandomTrackUseCase(get())
    }
    single<GetTopArtistsUseCase> {
        GetTopArtistsUseCase(get())
    }
}