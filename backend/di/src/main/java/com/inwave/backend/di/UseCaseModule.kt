package com.inwave.backend.di

import com.inwave.domain.usecase.artist.query.GetArtistAlbumsUseCase
import com.inwave.domain.usecase.artist.query.GetArtistReleasesUseCase
import com.inwave.domain.usecase.artist.query.GetArtistSinglesUseCase
import com.inwave.domain.usecase.artist.query.GetArtistTopTracksUseCase
import com.inwave.domain.usecase.artist.query.GetArtistUseCase
import com.inwave.domain.usecase.artist.query.GetTopArtistsUseCase
import com.inwave.domain.usecase.release.query.GetReleaseTracksUseCase
import com.inwave.domain.usecase.release.query.GetReleaseUseCase
import com.inwave.domain.usecase.track.query.GetRandomTrackUseCase
import com.inwave.domain.usecase.track.query.GetTrackUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single<GetReleaseUseCase> {
        GetReleaseUseCase(get())
    }
    single<GetReleaseTracksUseCase> {
        GetReleaseTracksUseCase(get())
    }

    single<GetArtistUseCase> {
        GetArtistUseCase(get(), get())
    }

    single<GetTopArtistsUseCase> {
        GetTopArtistsUseCase(get())
    }

    single<GetArtistSinglesUseCase> {
        GetArtistSinglesUseCase(get())
    }

    single<GetArtistAlbumsUseCase> {
        GetArtistAlbumsUseCase(get())
    }

    single<GetArtistReleasesUseCase> {
        GetArtistReleasesUseCase(get())
    }

    single<GetArtistTopTracksUseCase> {
        GetArtistTopTracksUseCase(get())
    }

    single<GetTrackUseCase> {
        GetTrackUseCase(get(), get())
    }

    single<GetRandomTrackUseCase> {
        GetRandomTrackUseCase(get())
    }
    single<GetTopArtistsUseCase> {
        GetTopArtistsUseCase(get())
    }
}