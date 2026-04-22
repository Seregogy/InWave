package com.inwave.di.repository

import android.content.Context
import com.invawe.data.repository.track.TrackQueryRepositoryLegacyImpl
import com.invawe.data.repository.track.TrackQueryRepositoryFileStorageImpl
import com.inwave.di.LocalRepo
import com.inwave.di.RemoteLegacyRepo
import com.inwave.domain.repository.query.TrackQueryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TrackRepositoryModule {
    @Provides
    @Singleton
    @LocalRepo
    fun provideTrackRepositoryLocal(
        @ApplicationContext context: Context
    ): TrackQueryRepository = TrackQueryRepositoryFileStorageImpl(context)

    @Provides
    @Singleton
    @RemoteLegacyRepo
    fun provideTrackRepositoryRemoteLegacy(
        httpClient: HttpClient
    ): TrackQueryRepository = TrackQueryRepositoryLegacyImpl(httpClient)
}