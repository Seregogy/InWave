package com.inwave.di.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.palette.graphics.Palette
import com.inwave.domain.cache.CacheRepository
import com.inwave.tool.ImagePaletteExtractor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {
    @Provides
    fun provideImagePaletteExtractor(
        @ApplicationContext context: Context,
        cacheRepository: CacheRepository<String, Pair<Bitmap, Palette>>
    ): ImagePaletteExtractor {
        return ImagePaletteExtractor(context, cacheRepository)
    }
}