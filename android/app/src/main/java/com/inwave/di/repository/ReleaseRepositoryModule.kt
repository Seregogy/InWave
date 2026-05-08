package com.inwave.di.repository

import com.invawe.data.repository.release.ReleaseQueryRepositoryImpl
import com.invawe.data.repository.release.ReleaseQueryRepositoryLegacyImpl
import com.inwave.di.RemoteLegacyRepo
import com.inwave.di.RemoteRepo
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
    @RemoteLegacyRepo
    fun provideReleaseQueryRepositoryRemoteLegacy(
        httpClient: HttpClient,
        @RemoteLegacyRepo artistRepository: ArtistQueryRepository
    ): ReleaseQueryRepository {
        return ReleaseQueryRepositoryLegacyImpl(httpClient, artistRepository)
    }

    @Provides
    @Singleton
    @RemoteRepo
    fun provideReleaseQueryRepositoryRemote(
        httpClient: HttpClient,
    ): ReleaseQueryRepository {
        return ReleaseQueryRepositoryImpl(httpClient)
    }
}