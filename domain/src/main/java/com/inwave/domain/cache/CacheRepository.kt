package com.inwave.domain.cache

interface CacheRepository<Key, Data> {
    suspend fun put(data: Pair<Key, Data>): Boolean
    suspend fun get(key: Key): Data?
    suspend fun remove(key: Key): Boolean
}