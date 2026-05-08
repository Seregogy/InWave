package com.invawe.data.repository.artist

import com.inwave.api.dto.artist.ArtistSummaryDto
import com.inwave.api.dto.artist.FullArtistDto
import com.inwave.api.dto.map.toDomain
import com.inwave.api.dto.release.FullReleaseDto
import com.inwave.api.dto.release.ReleaseSummaryDto
import com.inwave.api.dto.track.TrackSummaryDto
import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.query.ArtistQueryRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ArtistQueryRepositoryImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = "/v1"
) : ArtistQueryRepository {

    override suspend fun getArtist(artistId: String): Result<Artist> = runCatching {
        httpClient.get("$baseUrl/artists/$artistId")
            .body<FullArtistDto>()
            .toDomain()
    }

    override suspend fun getTopArtists(limit: Int): Result<List<Artist>> = runCatching {
        httpClient.get("$baseUrl/artists/top") { parameter("limit", limit) }
            .body<List<ArtistSummaryDto>>()
            .map { it.toDomain() }
    }

    override suspend fun getArtistReleases(artistId: String): Result<List<Release>> = runCatching {
        httpClient.get("$baseUrl/artists/$artistId/releases")
            .body<List<FullReleaseDto>>()
            .map { it.toDomain() }
    }

    override suspend fun getArtistTopTracks(artistId: String, limit: Int): Result<List<Track>> = runCatching {
        httpClient.get("$baseUrl/artists/$artistId/tracks/top")
            .body<List<TrackSummaryDto>>()
            .map { it.toDomain() }
    }

    override suspend fun getArtistSingles(artistId: String): Result<List<Release>> = runCatching {
        httpClient.get("$baseUrl/artists/$artistId/releases/singles")
            .body<List<ReleaseSummaryDto>>()
            .map { it.toDomain() }
    }

    override suspend fun getArtistLastRelease(artistId: String): Result<Release> = runCatching {
        httpClient.get("$baseUrl/artists/$artistId/releases/last")
            .body<FullReleaseDto>()
            .toDomain()
    }
}