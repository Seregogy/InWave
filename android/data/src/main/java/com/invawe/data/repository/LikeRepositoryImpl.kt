package com.invawe.data.repository

import com.inwave.api.dto.release.ReleaseToggleLikeResponse
import com.inwave.api.dto.track.TrackToggleLikeResponse
import com.inwave.domain.repository.command.LikeRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.http.HttpHeaders

class LikeRepositoryImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = "/v1"
) : LikeRepository {
    override suspend fun toggleLikeToTrack(
        authToken: String,
        trackId: String
    ): Result<Boolean> = runCatching {
        httpClient.post("$baseUrl/tracks/$trackId/like") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $authToken")
            }
        }.body<TrackToggleLikeResponse>().liked
    }

    override suspend fun toggleLikeToRelease(
        authToken: String,
        releaseId: String
    ): Result<Boolean> = runCatching {
        httpClient.post("$baseUrl/tracks/$releaseId/like") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $authToken")
            }
        }.body<ReleaseToggleLikeResponse>().liked
    }
}