package com.inwave.di.usecase

import com.inwave.domain.repository.query.ReleaseQueryRepository
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