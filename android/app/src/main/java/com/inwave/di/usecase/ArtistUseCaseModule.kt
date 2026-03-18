package com.inwave.di.usecase

import com.inwave.domain.repository.query.ArtistQueryRepository
import com.inwave.domain.usecase.artist.query.GetArtistAlbumsUseCase
import com.inwave.domain.usecase.artist.query.GetArtistLastReleaseUseCase
import com.inwave.domain.usecase.artist.query.GetArtistReleasesUseCase
import com.inwave.domain.usecase.artist.query.GetArtistSinglesUseCase
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
    fun providesGetArtistAlbumsUseCase(
        repository: ArtistQueryRepository
    ): GetArtistAlbumsUseCase {
        return GetArtistAlbumsUseCase(repository)
    }

    @Provides
    @Singleton
    fun providesGetArtistLastReleaseUseCase(
        repository: ArtistQueryRepository
    ): GetArtistLastReleaseUseCase {
        return GetArtistLastReleaseUseCase(repository)
    }
    @Provides
    @Singleton
    fun providesGetArtistReleasesUseCase(
        repository: ArtistQueryRepository
    ): GetArtistReleasesUseCase {
        return GetArtistReleasesUseCase(repository)
    }
    @Provides
    @Singleton
    fun providesGetArtistSinglesUseCase(
        repository: ArtistQueryRepository
    ): GetArtistSinglesUseCase {
        return GetArtistSinglesUseCase(repository)
    }
}