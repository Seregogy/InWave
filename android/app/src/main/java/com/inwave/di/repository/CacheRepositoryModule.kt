package com.inwave.di.repository

import android.graphics.Bitmap
import androidx.palette.graphics.Palette
import com.invawe.data.repository.cache.GenericMemoryCacheRepository
import com.inwave.di.ArtistCache
import com.inwave.di.LyricsCache
import com.inwave.di.PaletteCache
import com.inwave.di.TrackCache
import com.inwave.domain.cache.CacheRepository
import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Track.Lyrics
import com.inwave.domain.entity.Track
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CacheRepositoryModule {
    @Provides
    @Singleton
    @TrackCache
    fun provideTrackCacheRepository(): CacheRepository<@JvmSuppressWildcards String, @JvmSuppressWildcards Track> {
        return GenericMemoryCacheRepository()
    }

    @Provides
    @Singleton
    @ArtistCache
    fun provideArtistCacheRepository(): CacheRepository<@JvmSuppressWildcards String, @JvmSuppressWildcards Artist> {
        return GenericMemoryCacheRepository()
    }

    @Provides
    @Singleton
    @LyricsCache
    fun provideLyricsCacheRepository(): CacheRepository<@JvmSuppressWildcards String, @JvmSuppressWildcards Lyrics> {
        return GenericMemoryCacheRepository()
    }

    @Provides
    @Singleton
    @PaletteCache
    fun provideImagePaletteCacheRepository(): CacheRepository<@JvmSuppressWildcards String, @JvmSuppressWildcards Pair<Bitmap, Palette>> {
        return GenericMemoryCacheRepository()
    }
}