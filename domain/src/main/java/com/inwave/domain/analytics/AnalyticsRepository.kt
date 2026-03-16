package com.inwave.domain.analytics

interface AnalyticsRepository {
    suspend fun listenedTrack(id: String): Result<Boolean>
}