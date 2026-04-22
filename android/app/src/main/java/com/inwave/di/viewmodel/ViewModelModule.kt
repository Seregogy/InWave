package com.inwave.di.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.palette.graphics.Palette
import com.invawe.data.repository.cache.GenericMemoryCacheRepository
import com.inwave.di.PaletteCache
import com.inwave.domain.cache.CacheRepository
import com.inwave.tool.ImagePaletteExtractor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {
    /*@Provides
    @Singleton
    fun provideImagePaletteExtractor(
        @ApplicationContext context: Context
    ): ImagePaletteExtractor {

        return ImagePaletteExtractor(context, GenericMemoryCacheRepository())
    }*/
}