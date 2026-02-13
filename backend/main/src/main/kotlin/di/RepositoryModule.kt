package com.inwave.backend.di

import com.inwave.backend.data.repository.track.TrackRepositoryTestImpl
import com.inwave.domain.repository.TrackRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<TrackRepository> {
        TrackRepositoryTestImpl()
    }
}