package com.inwave.di.usecase

import com.inwave.di.LocalRepo
import com.inwave.di.LyricsCache
import com.inwave.di.RemoteRepo
import com.inwave.di.TrackCache
import com.inwave.domain.cache.CacheRepository
import com.inwave.domain.entity.Track
import com.inwave.domain.entity.Track.Lyrics
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
    @RemoteRepo
    fun provideGetAllTracksUseCaseRemoteLegacy(
        @RemoteRepo repository: TrackQueryRepository
    ): GetAllTracksUseCase = GetAllTracksUseCase(repository)

    @Provides
    @LocalRepo
    fun provideGetAllTracksUseCaseLocal(
        @LocalRepo repository: TrackQueryRepository
    ): GetAllTracksUseCase = GetAllTracksUseCase(repository)

    @Provides
    @RemoteRepo
    fun provideGetRandomTrackIdUseCaseRemoteLegacy(
        @RemoteRepo repository: TrackQueryRepository
    ): GetRandomTrackIdUseCase = GetRandomTrackIdUseCase(repository)

    @Provides
    @RemoteRepo
    fun providesGetRandomTrackUseCaseRemoteLegacy(
        @RemoteRepo repository: TrackQueryRepository
    ): GetRandomTrackUseCase = GetRandomTrackUseCase(repository)

    @Provides
    @RemoteRepo
    fun provideGetTrackLyricsUseCaseRemoteLegacy(
        @RemoteRepo repository: TrackQueryRepository,
        @LyricsCache cacheRepository: CacheRepository<@JvmSuppressWildcards String, @JvmSuppressWildcards Lyrics>
    ): GetTrackLyricsUseCase = GetTrackLyricsUseCase(repository, cacheRepository)

    @Provides
    @RemoteRepo
    fun provideGetTracksUseCaseRemoteLegacy(
        @RemoteRepo repository: TrackQueryRepository,
        @TrackCache cacheRepository: CacheRepository<@JvmSuppressWildcards String, @JvmSuppressWildcards Track>
    ): GetTracksUseCase = GetTracksUseCase(repository, cacheRepository)

    @Provides
    @RemoteRepo
    fun provideGetTrackUseCaseRemoteLegacy(
        @RemoteRepo repository: TrackQueryRepository,
        @TrackCache cacheRepository: CacheRepository<@JvmSuppressWildcards String, @JvmSuppressWildcards Track>
    ): GetTrackUseCase = GetTrackUseCase(repository, cacheRepository)

    @Provides
    @RemoteRepo
    fun provideGetTrackWithLyricsUseCaseRemoteLegacy(
        @RemoteRepo trackUseCase: GetTrackUseCase,
        @RemoteRepo trackLyricsUseCase: GetTrackLyricsUseCase
    ): GetTrackWithLyricsUseCase = GetTrackWithLyricsUseCase(trackUseCase, trackLyricsUseCase)
}