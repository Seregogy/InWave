package com.invawe.data.repository.track

import android.util.Log
import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track
import com.inwave.domain.entity.Track.ArtistOnTrack
import com.inwave.domain.entity.Track.ArtistType
import com.inwave.domain.entity.Track.Lyrics
import com.inwave.domain.repository.query.TrackQueryRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

object TrackMappers {

    // Для /api/v1/tracks/{id}
    @Serializable
    data class FullTrackResponse(
        val id: String,
        val name: String,
        val imageUrl: String? = null,
        val indexInAlbum: Int? = 0,
        val durationSeconds: Long? = null,
        val lyrics: String? = null,
        val isExplicit: Boolean = false,
        val audioUrl: String? = null,
        val album: AlbumDto? = null,
        val artists: List<ArtistDto>? = null
    ) {
        fun toDomain(): Track {
            val trackArtists = artists?.map { artist ->
                ArtistOnTrack(
                    artist = artist.toDomain(),
                    artistType = ArtistType.Primary
                )
            } ?: emptyList()

            return Track(
                id = id,
                releaseId = album?.id,
                name = name,
                coverArtUrl = imageUrl,
                audioUrl = audioUrl ?: "",
                durationMs = durationSeconds?.times(1000),
                isExplicit = isExplicit,
                placeInRelease = indexInAlbum,
                genres = emptyList(),
                metadata = null,
                statistics = null,
                lyrics = lyrics?.let {
                    Track.Lyrics(
                        plainText = it,
                        syncedText = null,
                        provider = null
                    )
                },
                additionalData = null,
                artists = trackArtists,
                release = null
            )
        }
    }

    // Для /api/v1/tracks (POST - несколько треков)
    @Serializable
    data class TracksListResponse(
        val tracks: List<FullTrackResponse>
    )

    // Для /api/v1/tracks/random
    @Serializable
    data class RandomTrackResponse(
        val id: String,
        val name: String,
        val imageUrl: String? = null,
        val indexInAlbum: Int? = 0,
        val durationSeconds: Long? = null,
        val lyrics: String? = null,
        val isExplicit: Boolean = false,
        val audioUrl: String? = null,
        val album: AlbumDto? = null,
        val artists: List<ArtistDto>? = null
    ) {
        fun toDomain(): Track {
            val trackArtists = artists?.map { artist ->
                ArtistOnTrack(
                    artist = artist.toDomain(),
                    artistType = ArtistType.Primary
                )
            } ?: emptyList()

            return Track(
                id = id,
                releaseId = album?.id,
                name = name,
                coverArtUrl = imageUrl,
                audioUrl = audioUrl ?: "",
                durationMs = durationSeconds?.times(1000),
                isExplicit = isExplicit,
                placeInRelease = indexInAlbum,
                genres = emptyList(),
                metadata = null,
                statistics = null,
                lyrics = lyrics?.let {
                    Track.Lyrics(
                        plainText = it,
                        syncedText = null,
                        provider = null
                    )
                },
                additionalData = null,
                artists = trackArtists,
                release = null
            )
        }
    }

    // Для /api/v1/tracks/random/id
    @Serializable
    data class RandomTrackIdResponse(
        val id: String
    )

    @Serializable
    data class AlbumDto(
        val id: String,
        val name: String,
        val imageUrl: String? = null,
        val artists: List<ArtistDto>? = null
    ) {
        fun toDomain(trackList: List<Track> = emptyList()): Release {
            return Release(
                id = id,
                name = name,
                coverArtUrl = imageUrl,
                releaseDate = null,
                tracks = trackList,
                genres = listOf(),
                statistics = null,
                additionalData = Release.AdditionalData(
                    fullTitle = null,
                    descriptionMarkdown = null,
                    descriptionPreviewPlainText = null,
                    label = null,
                    tags = listOf(),
                    credits = mapOf()
                ),
                artists = artists?.map { it.toDomain() }?.toList() ?: listOf()
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

    @Serializable
    data class Lyrics(
        val plainText: String? = null,
        val syncedText: Map<Long, String>? = null,
        val provider: String? = null
    ) {
        fun toDomain(): Track.Lyrics {
            Log.d("PlayerStateSource", this.toString())
            return Track.Lyrics(
                plainText = plainText,
                syncedText = syncedText,
                provider = provider
            )
        }
    }
}

class TrackQueryRepositoryLegacyImpl(
    private val client: HttpClient
) : TrackQueryRepository {

    override suspend fun getTrack(id: String): Result<Track> = runCatching {
        val response = client.get("/api/v1/tracks/$id")
        val data = response.body<TrackMappers.FullTrackResponse>()
        data.toDomain()
    }

    override suspend fun getTracks(ids: List<String>): Result<List<Track>> = runCatching {
        val response = client.post("/api/v1/tracks") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("tracks" to ids))
        }

        val data = response.body<TrackMappers.TracksListResponse>()
        data.tracks.map { it.toDomain() }
    }

    override suspend fun getAllTracks(page: Int, size: Int): Result<List<Track>> = runCatching {
        listOf()
    }

    override suspend fun getRandomTrack(): Result<Track> = runCatching {
        val response = client.get("/api/v1/tracks/random")
        val data = response.body<TrackMappers.RandomTrackResponse>()
        data.toDomain()
    }

    override suspend fun getRandomTrackId(): Result<String> = runCatching {
        val response = client.get("/api/v1/tracks/random/id")
        val data = response.body<TrackMappers.RandomTrackIdResponse>()
        data.id
    }

    override suspend fun getTrackLyrics(id: String): Result<Lyrics> = runCatching {
        client.get("/api/v1/lyrics/${id}")
            .body<TrackMappers.Lyrics>()
            .toDomain()
    }

    override suspend fun getTrackWithLyrics(id: String): Result<Track> = runCatching {
        val track = getTrack(id).getOrThrow()
        if (track.hasLyrics && track.lyrics == null) {
            throw IllegalStateException("Track has lyrics but lyrics data is missing")
        }
        track
    }

    override suspend fun searchTracks(query: String, limit: Int): Result<List<Track>> = runCatching {
        listOf()
    }
}