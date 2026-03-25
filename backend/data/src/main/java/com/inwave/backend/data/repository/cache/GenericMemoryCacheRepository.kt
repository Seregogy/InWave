package com.inwave.backend.data.repository.cache

import com.inwave.domain.cache.CacheRepository

class GenericMemoryCacheRepository<Key, Data>: CacheRepository<Key, Data> {
    private val cache: MutableMap<Key, Data> = mutableMapOf()

    override suspend fun put(data: Pair<Key, Data>): Boolean {
        return runCatching {
            this.cache[data.first] = data.second
        }.isSuccess
    }

    override suspend fun get(key: Key): Data? {
        if (contains(key))
            return null

        return cache[key]
    }

    override suspend fun contains(key: Key): Boolean {
        return cache[key] != null
    }

    override suspend fun remove(key: Key): Boolean {
        return runCatching {
            cache.remove(key)
        }.isSuccess
    }
}