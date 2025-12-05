package com.invawe.data.repository.cache

import android.util.LruCache
import com.inwave.domain.cache.CacheRepository

class GenericMemoryCacheRepository<Key, Data>: CacheRepository<Key, Data> {
    private val cache: LruCache<Key, Data> = LruCache<Key, Data>(100)

    override suspend fun put(data: Pair<Key, Data>): Boolean {
        return runCatching {
            this.cache.put(data.first, data.second)
        }.isSuccess
    }

    override suspend fun get(key: Key): Data? {
        if (contains(key))
            return null

        return cache.get(key)
    }

    override suspend fun contains(key: Key): Boolean {
        return cache.get(key) != null
    }

    override suspend fun remove(key: Key): Boolean {
        return runCatching {
            cache.remove(key)
        }.isSuccess
    }
}