package com.inwave.di.repository

import com.invawe.data.repository.artist.ArtistQueryRepositoryImpl
import com.invawe.data.repository.artist.ArtistQueryRepositoryLegacyImpl
import com.inwave.di.RemoteLegacyRepo
import com.inwave.di.RemoteRepo
import com.inwave.domain.repository.query.ArtistQueryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ArtistRepositoryModule {
    @Provides
    @Singleton
    @RemoteLegacyRepo
    fun provideLegacyRemoteArtistQueryRepository(
        httpClient: HttpClient
    ): ArtistQueryRepository {
        return ArtistQueryRepositoryLegacyImpl(httpClient)
    }

    @Provides
    @Singleton
    @RemoteRepo
    fun provideRemoteArtistQueryRepository(
        httpClient: HttpClient
    ): ArtistQueryRepository {
        return ArtistQueryRepositoryImpl(httpClient)
    }
}