package com.invawe.data.repository.artist

import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Statistics
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.query.ArtistQueryRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.Serializable

object ArtistMappers {

    @Serializable
    // Для /api/v1/artists/{id}
    data class ArtistResponse(
        val artist: ArtistDto
    )

    @Serializable
    data class ArtistDto(
        val id: String,
        val name: String,
        val about: String? = null,
        val listeningInMonth: Int? = null,
        val images: List<String>? = null,
        val imageUrl: String? = null  // для top artists
    ) {
        fun toDomain(): Artist {
            val a = Artist(
                id = id,
                name = name,
                about = about,
                genres = emptyList(),
                imagesUrl = images ?: listOf(imageUrl ?: ""),
                statistics = Statistics(
                    playCount = listeningInMonth ?: 0,
                    likeCount = 0,
                    repostCount = 0
                ),
                releases = emptyList()
            )
            println(a)
            return a
        }
    }
    @Serializable

    // Для /api/v1/artists/top
    data class TopArtistsResponse(
        val artists: List<ArtistDto>
    )
    @Serializable

    // Для /api/v1/artists/{id}/releases
    data class ReleasesResponse(
        val releases: List<ReleaseDto>
    )
    @Serializable

    data class ReleaseDto(
        val id: String,
        val name: String,
        val imageUrl: String?,
        val artists: List<ArtistDto>
    ) {
        fun toDomain(tracks: List<Track> = emptyList()): Release {
            return Release(
                id = id,
                name = name,
                coverArtUrl = imageUrl,
                releaseDate = null,
                tracks = tracks,
                artists = artists.map { it.toDomain() },
                genres = emptyList(),
                statistics = null,
                additionalData = null
            )
        }
    }
    @Serializable

    // Для /api/v1/artists/{id}/tracks/top
    data class TopTracksResponse(
        val tracks: List<TrackDto>
    )
    @Serializable

    data class TrackDto(
        val id: String,
        val name: String,
        val imageUrl: String? = null,
        val indexInAlbum: Int? = 0,
        val artists: List<ArtistDto> = listOf(),
        val audioUrl: String? = null
    ) {
        fun toDomain(): Track {
            return Track(
                id = id,
                releaseId = null,
                name = name,
                coverArtUrl = imageUrl,
                audioUrl = audioUrl ?: "",
                durationMs = null,
                isExplicit = false,
                placeInRelease = indexInAlbum,
                genres = emptyList(),
                metadata = null,
                statistics = null,
                hasLyrics = false,
                lyrics = null,
                additionalData = null,
                artists = artists.map {
                    Track.ArtistOnTrack(
                        artist = it.toDomain(),
                        artistType = if (it.id == artists.firstOrNull()?.id)
                            Track.ArtistType.Primary
                        else
                            Track.ArtistType.Featured
                    )
                }
            )
        }
    }
    @Serializable

    // Для /api/v1/artists/{id}/singles
    data class SinglesResponse(
        val singles: List<ReleaseDto>
    )
    @Serializable


    // Для /api/v1/artists/{id}/albums/latest
    data class LatestReleaseResponse(
        val lastAlbum: ReleaseDto,
        val releaseDate: Long
    ) {
        fun toDomain(): Pair<Release, Long> {
            return lastAlbum.toDomain() to releaseDate
        }
    }
}

class ArtistQueryRepositoryLegacyImpl(
    private val client: HttpClient,
) : ArtistQueryRepository {

    override suspend fun getArtist(artistId: String): Result<Artist> = runCatching {
        val response = client.get("/api/v1/artists/$artistId")
        val data = response.body<ArtistMappers.ArtistResponse>()
        data.artist.toDomain()
    }

    override suspend fun getTopArtists(limit: Int): Result<List<Artist>> = runCatching {
        val response = client.get("/api/v1/artists/top") {
            parameter("limit", limit)
        }
        val data = response.body<ArtistMappers.TopArtistsResponse>()
        data.artists.map { it.toDomain() }
    }

    override suspend fun getArtistReleases(artistId: String): Result<List<Release>> = runCatching {
        val response = client.get("/api/v1/artists/$artistId/releases")
        val data = response.body<ArtistMappers.ReleasesResponse>()
        data.releases.map { it.toDomain() }
    }

    override suspend fun getArtistTopTracks(artistId: String, limit: Int): Result<List<Track>> = runCatching {
        val response = client.get("/api/v1/artists/$artistId/tracks/top") {
            parameter("limit", limit)
        }
        val data = response.body<ArtistMappers.TopTracksResponse>()
        data.tracks.map { it.toDomain() }
    }

    override suspend fun getArtistSingles(artistId: String): Result<List<Release>> = runCatching {
        val response = client.get("/api/v1/artists/$artistId/singles")
        val data = response.body<ArtistMappers.SinglesResponse>()
        data.singles.map { it.toDomain() }
    }

    override suspend fun getArtistLastRelease(artistId: String): Result<Pair<Release, Long>> = runCatching {
        val response = client.get("/api/v1/artists/$artistId/albums/latest")
        val data = response.body<ArtistMappers.LatestReleaseResponse>()
        data.toDomain()
    }
}