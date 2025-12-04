package com.invawe.data.repository.cache

import android.graphics.Bitmap
import androidx.palette.graphics.Palette
import com.inwave.domain.cache.CacheRepository

class MemoryImagePaletteCacheRepository: CacheRepository<String, Pair<Bitmap, Palette>> {
    private val imageCacheMap: MutableMap<String, Pair<Bitmap, Palette>> = mutableMapOf()

    override suspend fun put(data: Pair<String, Pair<Bitmap, Palette>>): Boolean {
        return runCatching {
            imageCacheMap[data.first] = data.second
        }.isSuccess
    }

    override suspend fun get(key: String): Pair<Bitmap, Palette>? {
        if (imageCacheMap.containsKey(key).not())
            return null

        return imageCacheMap[key]
    }

    override suspend fun contains(key: String): Boolean {
        return imageCacheMap.containsKey(key)
    }

    override suspend fun remove(key: String): Boolean {
        return runCatching {
            imageCacheMap.remove(key)
        }.isSuccess
    }
}