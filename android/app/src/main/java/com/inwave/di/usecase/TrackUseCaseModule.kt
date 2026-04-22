package com.inwave.di.usecase

import com.inwave.di.LocalRepo
import com.inwave.di.LyricsCache
import com.inwave.di.RemoteLegacyRepo
import com.inwave.di.TrackCache
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
    @RemoteLegacyRepo
    fun provideGetAllTracksUseCaseRemoteLegacy(
        @RemoteLegacyRepo repository: TrackQueryRepository
    ): GetAllTracksUseCase = GetAllTracksUseCase(repository)

    @Provides
    @LocalRepo
    fun provideGetAllTracksUseCaseLocal(
        @LocalRepo repository: TrackQueryRepository
    ): GetAllTracksUseCase = GetAllTracksUseCase(repository)

    @Provides
    @RemoteLegacyRepo
    fun provideGetRandomTrackIdUseCaseRemoteLegacy(
        @RemoteLegacyRepo repository: TrackQueryRepository
    ): GetRandomTrackIdUseCase = GetRandomTrackIdUseCase(repository)

    @Provides
    @RemoteLegacyRepo
    fun providesGetRandomTrackUseCaseRemoteLegacy(
        @RemoteLegacyRepo repository: TrackQueryRepository
    ): GetRandomTrackUseCase = GetRandomTrackUseCase(repository)

    @Provides
    @RemoteLegacyRepo
    fun provideGetTrackLyricsUseCaseRemoteLegacy(
        @RemoteLegacyRepo repository: TrackQueryRepository,
        @LyricsCache cacheRepository: CacheRepository<@JvmSuppressWildcards String, @JvmSuppressWildcards Lyrics>
    ): GetTrackLyricsUseCase = GetTrackLyricsUseCase(repository, cacheRepository)

    @Provides
    @RemoteLegacyRepo
    fun provideGetTracksUseCaseRemoteLegacy(
        @RemoteLegacyRepo repository: TrackQueryRepository,
        @TrackCache cacheRepository: CacheRepository<@JvmSuppressWildcards String, @JvmSuppressWildcards Track>
    ): GetTracksUseCase = GetTracksUseCase(repository, cacheRepository)

    @Provides
    @RemoteLegacyRepo
    fun provideGetTrackUseCaseRemoteLegacy(
        @RemoteLegacyRepo repository: TrackQueryRepository,
        @TrackCache cacheRepository: CacheRepository<@JvmSuppressWildcards String, @JvmSuppressWildcards Track>
    ): GetTrackUseCase = GetTrackUseCase(repository, cacheRepository)

    @Provides
    @RemoteLegacyRepo
    fun provideGetTrackWithLyricsUseCaseRemoteLegacy(
        @RemoteLegacyRepo trackUseCase: GetTrackUseCase,
        @RemoteLegacyRepo trackLyricsUseCase: GetTrackLyricsUseCase
    ): GetTrackWithLyricsUseCase = GetTrackWithLyricsUseCase(trackUseCase, trackLyricsUseCase)
}