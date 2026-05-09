package com.invawe.data.repository.track

import com.inwave.api.dto.map.toDomain
import com.inwave.api.dto.track.FullTrackDto
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.query.TrackQueryRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class TrackQueryRepositoryImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = "/v1"
) : TrackQueryRepository {

    override suspend fun getTrack(id: String): Result<Track> = runCatching {
        httpClient.get("$baseUrl/tracks/$id")
            .body<FullTrackDto>()
            .toDomain()
    }

    override suspend fun getTracks(ids: List<String>): Result<List<Track>> = runCatching {
        ids.map { getTrack(it).getOrThrow() }
    }

    override suspend fun getAllTracks(page: Int, size: Int): Result<List<Track>> =
        Result.success(emptyList())

    override suspend fun getRandomTrack(): Result<Track> = runCatching {
        httpClient.get("$baseUrl/tracks/random")
            .body<FullTrackDto>()
            .toDomain()
    }

    override suspend fun getRandomTrackId(): Result<String> = runCatching {
        httpClient.get("$baseUrl/tracks/random/id")
            .body()
    }

    override suspend fun getTrackLyrics(id: String): Result<Track.Lyrics> = runCatching {
        getTrack(id).getOrThrow().lyrics!!
    }

    override suspend fun getTrackWithLyrics(id: String): Result<Track> =
        getTrack(id)

    override suspend fun searchTracks(query: String, limit: Int): Result<List<Track>> =
        Result.success(emptyList())
}