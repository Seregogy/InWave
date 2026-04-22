package com.inwave.di.usecase

import com.inwave.di.RemoteLegacyRepo
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
    @RemoteLegacyRepo
    fun providesGetReleaseTracksUseCaseRemoteLegacy(
        @RemoteLegacyRepo repository: ReleaseQueryRepository
    ): GetReleaseTracksUseCase {
        return GetReleaseTracksUseCase(repository)
    }

    @Provides
    @Singleton
    @RemoteLegacyRepo
    fun providesGetReleaseUseCaseRemoteLegacy(
        @RemoteLegacyRepo repository: ReleaseQueryRepository
    ): GetReleaseUseCase {
        return GetReleaseUseCase(repository)
    }

    @Provides
    @Singleton
    @RemoteLegacyRepo
    fun providesGetTopReleasesUseCaseRemoteLegacy(
        @RemoteLegacyRepo repository: ReleaseQueryRepository
    ): GetTopReleasesUseCase {
        return GetTopReleasesUseCase(repository)
    }
}