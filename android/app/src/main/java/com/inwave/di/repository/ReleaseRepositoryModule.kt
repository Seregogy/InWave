package com.inwave.di.repository

import com.invawe.data.repository.release.ReleaseQueryRepositoryLegacyImpl
import com.invawe.data.repository.release.ReleaseQueryRepositoryMock
import com.inwave.di.MockRepo
import com.inwave.di.RemoteLegacyRepo
import com.inwave.domain.repository.query.ArtistQueryRepository
import com.inwave.domain.repository.query.ReleaseQueryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReleaseRepositoryModule {
    @Provides
    @Singleton
    @MockRepo
    fun provideReleaseQueryRepositoryMock(): ReleaseQueryRepository {
        return ReleaseQueryRepositoryMock()
    }

    @Provides
    @Singleton
    @RemoteLegacyRepo
    fun provideReleaseQueryRepositoryRemoteLegacy(
        httpClient: HttpClient,
        @RemoteLegacyRepo artistRepository: ArtistQueryRepository
    ): ReleaseQueryRepository {
        return ReleaseQueryRepositoryLegacyImpl(httpClient, artistRepository)
    }
}