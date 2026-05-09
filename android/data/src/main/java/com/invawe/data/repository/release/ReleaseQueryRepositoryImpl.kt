package com.invawe.data.repository.release

import com.inwave.api.dto.map.toDomain
import com.inwave.api.dto.release.FullReleaseDto
import com.inwave.api.dto.track.FullTrackDto
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.query.ReleaseQueryRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ReleaseQueryRepositoryImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = "/v1"
) : ReleaseQueryRepository {
    override suspend fun getRelease(releaseId: String): Result<Release> = runCatching {
        httpClient.get("$baseUrl/releases/$releaseId")
            .body<FullReleaseDto>()
            .also { it }
            .toDomain()
    }

    override suspend fun getReleaseTracks(releaseId: String): Result<List<Track>> = runCatching {
        httpClient.get("$baseUrl/releases/$releaseId/tracks")
            .body<List<FullTrackDto>>()
            .also { it }
            .map { it.toDomain() }
    }

    override suspend fun getTopReleases(limit: Int): Result<List<Release>> {
        return try {
            //TODO("endpoint is not in the API spec, returning empty list")
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}