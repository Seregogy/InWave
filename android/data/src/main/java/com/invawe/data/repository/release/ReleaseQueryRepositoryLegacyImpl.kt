package com.invawe.data.repository.release

import com.invawe.data.repository.track.TrackMappers
import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.query.ArtistQueryRepository
import com.inwave.domain.repository.query.ReleaseQueryRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.Serializable

object ReleaseMappers {

    // Для /api/v1/albums/{id}
    @Serializable
    data class FullAlbumResponse(
        val id: String,
        val name: String,
        val imageUrl: String? = null,
        val artists: List<ArtistDto>? = null,
        val likes: String? = null,
        val listening: String? = null,
        val releaseDate: Long? = null,
        val label: String? = null,
        val tracks: List<TrackDto>? = null
    ) {
        fun toDomain(trackList: List<Track> = emptyList()): Release {
            return Release(
                id = id,
                name = name,
                coverArtUrl = imageUrl,
                releaseDate = releaseDate?.let {
                    java.time.Instant.ofEpochSecond(it)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()
                },
                tracks = trackList,
                artists = artists?.map { it.toDomain() } ?: emptyList(),
                genres = emptyList(),
                statistics = null,
                additionalData = Release.AdditionalData(
                    fullTitle = null,
                    descriptionMarkdown = null,
                    descriptionPreviewPlainText = null,
                    label = label,
                    tags = emptyList(),
                    credits = emptyMap()
                )
            )
        }
    }

    // Для /api/v1/albums/{id}/tracks
    @Serializable
    data class AlbumTracksResponse(
        val tracks: List<TrackDto>
    )

    @Serializable
    data class TrackDto(
        val id: String,
        val name: String,
        val imageUrl: String? = null,
        val indexInAlbum: Int? = 0,
        val durationSeconds: Long? = null,
        val isExplicit: Boolean = false,
        val audioUrl: String? = null,
        val artists: List<ArtistDto>? = null
    ) {
        fun toDomain(): Track {
            return Track(
                id = id,
                releaseId = null,
                name = name,
                coverArtUrl = imageUrl,
                audioUrl = audioUrl ?: "",
                durationMs = durationSeconds?.times(1000),
                isExplicit = isExplicit,
                placeInRelease = indexInAlbum,
                genres = emptyList(),
                metadata = null,
                statistics = null,
                lyrics = null,
                additionalData = null,
                artists = artists?.map { artist ->
                    Track.ArtistOnTrack(
                        artist = artist.toDomain(),
                        artistType = Track.ArtistType.Primary
                    )
                } ?: emptyList(),
                release = null
            )
        }
    }

    @Serializable
    data class ArtistDto(
        val id: String,
        val name: String,
        val about: String? = null,
        val imageUrl: String? = null
    ) {
        fun toDomain(): Artist {
            return Artist(
                id = id,
                name = name,
                about = about,
                genres = emptyList(),
                imagesUrl = listOfNotNull(imageUrl),
                statistics = null,
                releases = emptyList()
            )
        }
    }

    // Для /api/v1/artists/{artistId}/releases
    @Serializable
    data class ArtistReleases(
        val releases: List<TrackMappers.AlbumDto>
    )
}

class ReleaseQueryRepositoryLegacyImpl(
    private val client: HttpClient,
    private val artistRepository: ArtistQueryRepository
) : ReleaseQueryRepository {

    override suspend fun getRelease(releaseId: String): Result<Release> = runCatching {
        val response = client.get("/api/v1/albums/$releaseId")
        val data = response.body<ReleaseMappers.FullAlbumResponse>()

        val tracks = getReleaseTracks(releaseId).getOrNull() ?: emptyList()

        data.toDomain(tracks)
    }

    override suspend fun getReleaseTracks(releaseId: String): Result<List<Track>> = runCatching {
        val response = client.get("/api/v1/albums/$releaseId/tracks")
        val data = response.body<ReleaseMappers.AlbumTracksResponse>()
        data.tracks.map { it.toDomain() }
    }

    override suspend fun getTopReleases(limit: Int): Result<List<Release>> = runCatching {
        val artist = artistRepository.getTopArtists(1).getOrNull()?.firstOrNull()
            ?: error("failed to fetch artist")

        val response = client.get("/api/v1/artists/${artist.id}/releases")
        val data = response.body<ReleaseMappers.ArtistReleases>()

        data.releases.map { it.toDomain(listOf()) }
    }
}
