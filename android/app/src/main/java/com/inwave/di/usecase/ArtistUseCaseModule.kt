package com.inwave.di.usecase

import com.inwave.di.ArtistCache
import com.inwave.di.RemoteLegacyRepo
import com.inwave.domain.cache.CacheRepository
import com.inwave.domain.entity.Artist
import com.inwave.domain.repository.query.ArtistQueryRepository
import com.inwave.domain.usecase.artist.query.GetArtistAlbumsUseCase
import com.inwave.domain.usecase.artist.query.GetArtistLastReleaseUseCase
import com.inwave.domain.usecase.artist.query.GetArtistReleasesUseCase
import com.inwave.domain.usecase.artist.query.GetArtistSinglesUseCase
import com.inwave.domain.usecase.artist.query.GetArtistTopTracksUseCase
import com.inwave.domain.usecase.artist.query.GetArtistUseCase
import com.inwave.domain.usecase.artist.query.GetTopArtistsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ArtistUseCaseModule {
    @Provides
    @Singleton
    @RemoteLegacyRepo
    fun providesGetArtistUseCaseRemoteLegacy(
        @RemoteLegacyRepo repository: ArtistQueryRepository,
        @ArtistCache cacheRepository: CacheRepository<@JvmSuppressWildcards String, @JvmSuppressWildcards Artist>
    ): GetArtistUseCase {
        return GetArtistUseCase(repository, cacheRepository)
    }

    @Provides
    @Singleton
    @RemoteLegacyRepo
    fun providesGetArtistAlbumsUseCaseRemoteLegacy(
        @RemoteLegacyRepo repository: ArtistQueryRepository
    ): GetArtistAlbumsUseCase {
        return GetArtistAlbumsUseCase(repository)
    }

    @Provides
    @Singleton
    @RemoteLegacyRepo
    fun providesGetArtistLastReleaseUseCaseRemoteLegacy(
        @RemoteLegacyRepo repository: ArtistQueryRepository
    ): GetArtistLastReleaseUseCase {
        return GetArtistLastReleaseUseCase(repository)
    }

    @Provides
    @Singleton
    @RemoteLegacyRepo
    fun providesGetArtistReleasesUseCaseRemoteLegacy(
        @RemoteLegacyRepo repository: ArtistQueryRepository
    ): GetArtistReleasesUseCase {
        return GetArtistReleasesUseCase(repository)
    }

    @Provides
    @Singleton
    @RemoteLegacyRepo
    fun providesGetArtistSinglesUseCaseRemoteLegacy(
        @RemoteLegacyRepo repository: ArtistQueryRepository
    ): GetArtistSinglesUseCase {
        return GetArtistSinglesUseCase(repository)
    }

    @Provides
    @Singleton
    @RemoteLegacyRepo
    fun providesGetArtistTopTracksUseCaseRemoteLegacy(
        @RemoteLegacyRepo repository: ArtistQueryRepository
    ): GetArtistTopTracksUseCase {
        return GetArtistTopTracksUseCase(repository)
    }

    @Provides
    @Singleton
    @RemoteLegacyRepo
    fun providesGetTopArtistsUseCaseRemoteLegacy(
        @RemoteLegacyRepo repository: ArtistQueryRepository
    ): GetTopArtistsUseCase {
        return GetTopArtistsUseCase(repository)
    }
}