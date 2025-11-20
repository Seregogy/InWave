package com.inwave.di.usecase

import com.inwave.domain.cache.CacheRepository
import com.inwave.domain.entity.Lyrics
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.TrackRepository
import com.inwave.domain.usecase.track.GetAllTracksUseCase
import com.inwave.domain.usecase.track.GetRandomTrackIdUseCase
import com.inwave.domain.usecase.track.GetRandomTrackUseCase
import com.inwave.domain.usecase.track.GetTrackLyricsUseCase
import com.inwave.domain.usecase.track.GetTrackUseCase
import com.inwave.domain.usecase.track.GetTrackWithLyrics
import com.inwave.domain.usecase.track.GetTracksUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class TrackUseCaseModule {
    @Provides
    fun provideGetAllTracksUseCase(
        repository: TrackRepository
    ): GetAllTracksUseCase = GetAllTracksUseCase(repository)

    @Provides
    fun provideGetRandomTrackIdUseCase(
        repository: TrackRepository
    ): GetRandomTrackIdUseCase = GetRandomTrackIdUseCase(repository)

    @Provides
    fun providesGetRandomTrackUseCase(
        repository: TrackRepository
    ): GetRandomTrackUseCase = GetRandomTrackUseCase(repository)

    @Provides
    fun provideGetTrackLyricsUseCase(
        repository: TrackRepository,
        cacheRepository: CacheRepository<String, Lyrics>
    ): GetTrackLyricsUseCase = GetTrackLyricsUseCase(repository, cacheRepository)

    @Provides
    fun provideGetTracksUseCase(
        repository: TrackRepository,
        cacheRepository: CacheRepository<String, Track>
    ): GetTracksUseCase = GetTracksUseCase(repository, cacheRepository)

    @Provides
    fun provideGetTrackUseCase(
        repository: TrackRepository,
        cacheRepository: CacheRepository<String, Track>
    ): GetTrackUseCase = GetTrackUseCase(repository, cacheRepository)

    @Provides
    fun provideGetTrackWithLyricsUseCase(
        trackUseCase: GetTrackUseCase,
        trackLyricsUseCase: GetTrackLyricsUseCase
    ): GetTrackWithLyrics = GetTrackWithLyrics(trackUseCase, trackLyricsUseCase)
}