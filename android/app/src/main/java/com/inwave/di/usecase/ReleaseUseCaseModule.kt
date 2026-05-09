package com.inwave.di.usecase

import com.inwave.di.RemoteRepo
import com.inwave.domain.repository.query.ReleaseQueryRepository
import com.inwave.domain.usecase.release.query.GetReleaseTracksUseCase
import com.inwave.domain.usecase.release.query.GetReleaseUseCase
import com.inwave.domain.usecase.release.query.GetTopReleasesUseCase
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
    @RemoteRepo
    fun providesGetReleaseTracksUseCaseRemoteLegacy(
        @RemoteRepo repository: ReleaseQueryRepository
    ): GetReleaseTracksUseCase {
        return GetReleaseTracksUseCase(repository)
    }

    @Provides
    @Singleton
    @RemoteRepo
    fun providesGetReleaseUseCaseRemoteLegacy(
        @RemoteRepo repository: ReleaseQueryRepository
    ): GetReleaseUseCase {
        return GetReleaseUseCase(repository)
    }

    @Provides
    @Singleton
    @RemoteRepo
    fun providesGetTopReleasesUseCaseRemoteLegacy(
        @RemoteRepo repository: ReleaseQueryRepository
    ): GetTopReleasesUseCase {
        return GetTopReleasesUseCase(repository)
    }
}