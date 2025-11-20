package com.inwave.di.repository

import android.content.Context
import com.invawe.data.repository.track.TrackRepositoryFileStorageImpl
import com.inwave.domain.repository.TrackRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TrackRepositoryModule {
    @Provides
    @Singleton
    fun getTrackRepository(
        @ApplicationContext context: Context
    ): TrackRepository = TrackRepositoryFileStorageImpl(context)
}