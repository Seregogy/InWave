package com.inwave.backend.di

import com.inwave.backend.data.repository.artist.ArtistQueryRepositoryImpl
import com.inwave.backend.data.repository.release.ReleaseQueryRepositoryImpl
import com.inwave.backend.data.repository.track.TrackQueryRepositoryImpl
import com.inwave.domain.repository.query.ArtistQueryRepository
import com.inwave.domain.repository.query.ReleaseQueryRepository
import com.inwave.domain.repository.query.TrackQueryRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<TrackQueryRepository> {
        TrackQueryRepositoryImpl(get())
    }
    single<ReleaseQueryRepository> {
        ReleaseQueryRepositoryImpl(get())
    }
    single<ArtistQueryRepository> {
        ArtistQueryRepositoryImpl(get())
    }
}