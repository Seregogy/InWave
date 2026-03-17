package com.inwave.di.repository

import com.invawe.data.repository.release.ReleaseQueryRepositoryMock
import com.inwave.domain.repository.query.ReleaseQueryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReleaseRepositoryModule {
    @Provides
    @Singleton
    fun provideReleaseQueryRepository(): ReleaseQueryRepository {
        return ReleaseQueryRepositoryMock()
    }
}