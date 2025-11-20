package com.inwave.di.repository

import com.inwave.domain.cache.CacheRepository
import com.inwave.domain.entity.Lyrics
import com.inwave.domain.entity.Track
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CacheRepositoryModule {
    @Provides
    @Singleton
    fun provideTrackCacheRepository(): CacheRepository<String, Track> {
        TODO("Not implemented")
    }

    @Provides
    @Singleton
    fun provideLyricsCacheRepository(): CacheRepository<String, Lyrics> {
        TODO("Not implemented")
    }
}