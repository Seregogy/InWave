package com.inwave.di.usecase

import com.inwave.domain.repository.query.ReleaseQueryRepository
import com.inwave.domain.usecase.release.query.GetArtistAlbumsUseCase
import com.inwave.domain.usecase.release.query.GetArtistLastReleaseUseCase
import com.inwave.domain.usecase.release.query.GetArtistReleasesUseCase
import com.inwave.domain.usecase.release.query.GetArtistSinglesUseCase
import com.inwave.domain.usecase.release.query.GetReleaseTracksUseCase
import com.inwave.domain.usecase.release.query.GetReleaseUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReleaseUseCaseModule {
    @Provides
    @Singleton
    fun providesGetArtistAlbumsUseCase(
        repository: ReleaseQueryRepository
    ): GetArtistAlbumsUseCase {
        return GetArtistAlbumsUseCase(repository)
    }
    @Provides
    @Singleton
    fun providesGetArtistLastReleaseUseCase(
        repository: ReleaseQueryRepository
    ): GetArtistLastReleaseUseCase {
        return GetArtistLastReleaseUseCase(repository)
    }
    @Provides
    @Singleton
    fun providesGetArtistReleasesUseCase(
        repository: ReleaseQueryRepository
    ): GetArtistReleasesUseCase {
        return GetArtistReleasesUseCase(repository)
    }
    @Provides
    @Singleton
    fun providesGetArtistSinglesUseCase(
        repository: ReleaseQueryRepository
    ): GetArtistSinglesUseCase {
        return GetArtistSinglesUseCase(repository)
    }
    @Provides
    @Singleton
    fun providesGetReleaseTracksUseCase(
        repository: ReleaseQueryRepository
    ): GetReleaseTracksUseCase {
        return GetReleaseTracksUseCase(repository)
    }
    @Provides
    @Singleton
    fun providesGetReleaseUseCase(
        repository: ReleaseQueryRepository
    ): GetReleaseUseCase {
        return GetReleaseUseCase(repository)
    }
}