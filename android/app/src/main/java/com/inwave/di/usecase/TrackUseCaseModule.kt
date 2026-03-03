package com.inwave.di.usecase

import com.inwave.domain.cache.CacheRepository
import com.inwave.domain.entity.Track.Lyrics
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.query.TrackQueryRepository
import com.inwave.domain.usecase.track.query.GetAllTracksUseCase
import com.inwave.domain.usecase.track.query.GetRandomTrackIdUseCase
import com.inwave.domain.usecase.track.query.GetRandomTrackUseCase
import com.inwave.domain.usecase.track.query.GetTrackLyricsUseCase
import com.inwave.domain.usecase.track.query.GetTrackUseCase
import com.inwave.domain.usecase.track.query.GetTrackWithLyricsUseCase
import com.inwave.domain.usecase.track.query.GetTracksUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TrackUseCaseModule {
    @Provides
    fun provideGetAllTracksUseCase(
        repository: TrackQueryRepository
    ): GetAllTracksUseCase = GetAllTracksUseCase(repository)

    @Provides
    fun provideGetRandomTrackIdUseCase(
        repository: TrackQueryRepository
    ): GetRandomTrackIdUseCase = GetRandomTrackIdUseCase(repository)

    @Provides
    fun providesGetRandomTrackUseCase(
        repository: TrackQueryRepository
    ): GetRandomTrackUseCase = GetRandomTrackUseCase(repository)

    @Provides
    fun provideGetTrackLyricsUseCase(
        repository: TrackQueryRepository,
        cacheRepository: CacheRepository<String, Lyrics>
    ): GetTrackLyricsUseCase = GetTrackLyricsUseCase(repository, cacheRepository)

    @Provides
    fun provideGetTracksUseCase(
        repository: TrackQueryRepository,
        cacheRepository: CacheRepository<String, Track>
    ): GetTracksUseCase = GetTracksUseCase(repository, cacheRepository)

    @Provides
    fun provideGetTrackUseCase(
        repository: TrackQueryRepository,
        cacheRepository: CacheRepository<String, Track>
    ): GetTrackUseCase = GetTrackUseCase(repository, cacheRepository)

    @Provides
    fun provideGetTrackWithLyricsUseCase(
        trackUseCase: GetTrackUseCase,
        trackLyricsUseCase: GetTrackLyricsUseCase
    ): GetTrackWithLyricsUseCase = GetTrackWithLyricsUseCase(trackUseCase, trackLyricsUseCase)
}