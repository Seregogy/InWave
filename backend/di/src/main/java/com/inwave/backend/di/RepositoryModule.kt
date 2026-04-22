package com.inwave.backend.di

import com.inwave.backend.data.repository.MockArtistQueryRepository
import com.inwave.backend.data.repository.MockReleaseQueryRepository
import com.inwave.backend.data.repository.MockTrackQueryRepository
import com.inwave.backend.data.repository.cache.GenericMemoryCacheRepository
import com.inwave.domain.cache.CacheRepository
import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.query.ArtistQueryRepository
import com.inwave.domain.repository.query.ReleaseQueryRepository
import com.inwave.domain.repository.query.TrackQueryRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<TrackQueryRepository> {
        MockTrackQueryRepository()
    }

    single<ReleaseQueryRepository> {
        MockReleaseQueryRepository()
    }

    single<ArtistQueryRepository> {
        MockArtistQueryRepository()
    }

    single<CacheRepository<String, Artist>> {
        GenericMemoryCacheRepository()
    }

    single<CacheRepository<String, Track>> {
        GenericMemoryCacheRepository()
    }
}