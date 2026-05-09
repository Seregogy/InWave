package com.inwave.backend.data.repository

import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Statistics
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.query.ArtistQueryRepository
import com.inwave.domain.repository.query.ReleaseQueryRepository
import com.inwave.domain.repository.query.TrackQueryRepository
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.random.Random

class MockArtistQueryRepository : ArtistQueryRepository {

    private val mockArtists = generateMockArtists()

    override suspend fun getArtist(artistId: String): Result<Artist> {
        return try {
            val artist = mockArtists.find { it.id == artistId }
                ?: return Result.failure(NoSuchElementException("Artist not found: $artistId"))
            Result.success(artist)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTopArtists(limit: Int): Result<List<Artist>> {
        return try {
            val topArtists = mockArtists
                .sortedByDescending { it.statistics?.playCount ?: 0 }
                .take(limit)
            Result.success(topArtists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getArtistReleases(artistId: String): Result<List<Release>> {
        return try {
            val artist = mockArtists.find { it.id == artistId }
                ?: return Result.failure(NoSuchElementException("Artist not found: $artistId"))
            Result.success(artist.releases)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getArtistTopTracks(artistId: String, limit: Int): Result<List<Track>> {
        return try {
            val artist = mockArtists.find { it.id == artistId }
                ?: return Result.failure(NoSuchElementException("Artist not found: $artistId"))

            val allTracks = artist.releases.flatMap { release -> release.tracks }
            val topTracks = allTracks
                .sortedByDescending { it.statistics?.playCount ?: 0 }
                .take(limit)

            Result.success(topTracks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getArtistSingles(artistId: String): Result<List<Release>> {
        return try {
            val artist = mockArtists.find { it.id == artistId }
                ?: return Result.failure(NoSuchElementException("Artist not found: $artistId"))

            val singles = artist.releases.filter { release -> release.tracks.size <= 2 }
            Result.success(singles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getArtistLastRelease(artistId: String): Result<Release> {
        return try {
            val artist = mockArtists.find { it.id == artistId }
                ?: return Result.failure(NoSuchElementException("Artist not found: $artistId"))

            val lastRelease = artist.releases
                .maxByOrNull { it.releaseDate ?: LocalDate.MIN }
                ?: return Result.failure(NoSuchElementException("No releases found for artist: $artistId"))

            val daysSinceRelease = lastRelease.releaseDate?.let { releaseDate ->
                ChronoUnit.DAYS.between(releaseDate, LocalDate.now())
            } ?: 0L

            Result.success(lastRelease)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateMockArtists(): List<Artist> {
        return listOf(
            createMockArtist(
                id = "artist_1",
                name = "The Midnight",
                about = "Synthwave duo from Los Angeles",
                genres = listOf("Synthwave", "Retrowave"),
                imagesUrl = listOf("https://example.com/midnight_1.jpg", "https://example.com/midnight_2.jpg"),
                playCount = 1_500_000,
                likeCount = 120_000,
                repostCount = 45_000
            ),
            createMockArtist(
                id = "artist_2",
                name = "Gunship",
                about = "Synthwave band from UK",
                genres = listOf("Synthwave", "Darksynth"),
                imagesUrl = listOf("https://example.com/gunship_1.jpg"),
                playCount = 1_200_000,
                likeCount = 95_000,
                repostCount = 32_000
            ),
            createMockArtist(
                id = "artist_3",
                name = "Daft Punk",
                about = "French electronic music duo",
                genres = listOf("House", "Electronic"),
                imagesUrl = listOf("https://example.com/daftpunk_1.jpg", "https://example.com/daftpunk_2.jpg"),
                playCount = 5_000_000,
                likeCount = 500_000,
                repostCount = 150_000
            )
        )
    }

    private fun createMockArtist(
        id: String,
        name: String,
        about: String?,
        genres: List<String>,
        imagesUrl: List<String>,
        playCount: Int,
        likeCount: Int,
        repostCount: Int
    ): Artist {
        val statistics = Statistics(playCount, likeCount, repostCount)
        val releases = generateMockReleasesForArtist(id, name)

        return Artist(
            id = id,
            name = name,
            about = about,
            genres = genres,
            imagesUrl = imagesUrl,
            statistics = statistics,
            releases = releases
        )
    }

    private fun generateMockReleasesForArtist(artistId: String, artistName: String): List<Release> {
        return listOf(
            createMockRelease(
                id = "${artistId}_album_1",
                name = "First Light",
                artistId = artistId,
                artistName = artistName,
                releaseDate = LocalDate.of(2023, 1, 15),
                trackCount = 12,
                isAlbum = true
            ),
            createMockRelease(
                id = "${artistId}_album_2",
                name = "Night Drive",
                artistId = artistId,
                artistName = artistName,
                releaseDate = LocalDate.of(2024, 3, 20),
                trackCount = 10,
                isAlbum = true
            ),
            createMockRelease(
                id = "${artistId}_single_1",
                name = "Summer Nights",
                artistId = artistId,
                artistName = artistName,
                releaseDate = LocalDate.of(2024, 6, 10),
                trackCount = 1,
                isAlbum = false
            )
        )
    }
}

class MockReleaseQueryRepository : ReleaseQueryRepository {
    private val mockReleases = generateMockReleases()

    override suspend fun getRelease(releaseId: String): Result<Release> {
        return try {
            val release = mockReleases.find { it.id == releaseId }
                ?: return Result.failure(NoSuchElementException("Release not found: $releaseId"))
            Result.success(release)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReleaseTracks(releaseId: String): Result<List<Track>> {
        return try {
            val release = mockReleases.find { it.id == releaseId }
                ?: return Result.failure(NoSuchElementException("Release not found: $releaseId"))
            Result.success(release.tracks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTopReleases(limit: Int): Result<List<Release>> {
        TODO("Not yet implemented")
    }

    private fun generateMockReleases(): List<Release> {
        return listOf(
            createMockRelease(
                id = "release_1",
                name = "First Light",
                artistId = "artist_1",
                artistName = "The Midnight",
                releaseDate = LocalDate.of(2023, 1, 15),
                trackCount = 12,
                isAlbum = true
            ),
            createMockRelease(
                id = "release_2",
                name = "Dark All Day",
                artistId = "artist_2",
                artistName = "Gunship",
                releaseDate = LocalDate.of(2023, 5, 20),
                trackCount = 10,
                isAlbum = true
            ),
            createMockRelease(
                id = "release_3",
                name = "Random Access Memories",
                artistId = "artist_3",
                artistName = "Daft Punk",
                releaseDate = LocalDate.of(2013, 5, 17),
                trackCount = 13,
                isAlbum = true
            )
        )
    }
}

class MockTrackQueryRepository : TrackQueryRepository {

    private val mockTracks = generateMockTracks()

    override suspend fun getTrack(id: String): Result<Track> {
        return try {
            val track = mockTracks.find { it.id == id }
                ?: return Result.failure(NoSuchElementException("Track not found: $id"))
            Result.success(track)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTracks(ids: List<String>): Result<List<Track>> {
        return try {
            val tracks = ids.mapNotNull { id -> mockTracks.find { it.id == id } }
            Result.success(tracks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllTracks(page: Int, size: Int): Result<List<Track>> {
        return try {
            val start = page * size
            val end = minOf(start + size, mockTracks.size)
            val pagedTracks = mockTracks.subList(start, end)
            Result.success(pagedTracks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRandomTrack(): Result<Track> {
        return try {
            val randomIndex = Random.nextInt(mockTracks.size)
            Result.success(mockTracks[randomIndex])
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRandomTrackId(): Result<String> {
        return try {
            val randomIndex = Random.nextInt(mockTracks.size)
            Result.success(mockTracks[randomIndex].id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTrackLyrics(id: String): Result<Track.Lyrics> {
        return try {
            val track = mockTracks.find { it.id == id }
                ?: return Result.failure(NoSuchElementException("Track not found: $id"))

            val lyrics = track.lyrics
                ?: return Result.failure(NoSuchElementException("No lyrics found for track: $id"))

            Result.success(lyrics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTrackWithLyrics(id: String): Result<Track> {
        return try {
            val track = mockTracks.find { it.id == id }
                ?: return Result.failure(NoSuchElementException("Track not found: $id"))

            if (track.lyrics == null) {
                return Result.failure(NoSuchElementException("No lyrics found for track: $id"))
            }

            Result.success(track)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchTracks(query: String, limit: Int): Result<List<Track>> {
        return try {
            val matchedTracks = mockTracks
                .filter { track ->
                    track.name.contains(query, ignoreCase = true) ||
                            track.artists.any { it.artist.name.contains(query, ignoreCase = true) }
                }
                .take(limit)
            Result.success(matchedTracks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateMockTracks(): List<Track> {
        return listOf(
            createMockTrack(
                id = "track_1",
                name = "Sunset",
                releaseId = "release_1",
                artistId = "artist_1",
                artistName = "The Midnight",
                durationMs = 280000,
                playCount = 250_000,
                likeCount = 18_000,
                repostCount = 5_000,
                hasLyrics = true
            ),
            createMockTrack(
                id = "track_2",
                name = "Nightcall",
                releaseId = "release_2",
                artistId = "artist_2",
                artistName = "Gunship",
                durationMs = 320000,
                playCount = 180_000,
                likeCount = 12_000,
                repostCount = 3_500,
                hasLyrics = true
            ),
            createMockTrack(
                id = "track_3",
                name = "Get Lucky",
                releaseId = "release_3",
                artistId = "artist_3",
                artistName = "Daft Punk",
                durationMs = 368000,
                playCount = 1_200_000,
                likeCount = 95_000,
                repostCount = 28_000,
                hasLyrics = true
            )
        )
    }
}

// Вспомогательные функции для создания моковых данных
private fun createMockRelease(
    id: String,
    name: String,
    artistId: String,
    artistName: String,
    releaseDate: LocalDate,
    trackCount: Int,
    isAlbum: Boolean
): Release {
    val artist = Artist(
        id = artistId,
        name = artistName,
        about = null,
        genres = listOf("Electronic"),
        imagesUrl = listOf(),
        statistics = null,
        releases = emptyList()
    )

    val tracks = (1..trackCount).map { trackNumber ->
        createMockTrack(
            id = "${id}_track_$trackNumber",
            name = "$name Track $trackNumber",
            releaseId = id,
            artistId = artistId,
            artistName = artistName,
            durationMs = 180_000L + (trackNumber * 10_000),
            playCount = 100_000 / trackNumber,
            likeCount = 10_000 / trackNumber,
            repostCount = 5_000 / trackNumber,
            hasLyrics = trackNumber % 2 == 0,
            placeInRelease = trackNumber
        )
    }

    val statistics = Statistics(
        playCount = tracks.sumOf { it.statistics?.playCount ?: 0 },
        likeCount = tracks.sumOf { it.statistics?.likeCount ?: 0 },
        repostCount = tracks.sumOf { it.statistics?.repostCount ?: 0 }
    )

    val additionalData = Release.AdditionalData(
        fullTitle = "$artistName - $name",
        descriptionMarkdown = "This is a mock release for $name",
        descriptionPreviewPlainText = "Mock release description",
        label = "Mock Records",
        tags = listOf("electronic", "synthwave"),
        credits = mapOf("producer" to listOf(artistName))
    )

    return Release(
        id = id,
        name = name,
        coverArtUrl = "https://example.com/releases/$id.jpg",
        releaseDate = releaseDate,
        tracks = tracks,
        artists = listOf(artist),
        genres = listOf("Electronic", "Synthwave"),
        statistics = statistics,
        additionalData = additionalData
    )
}

private fun createMockTrack(
    id: String,
    name: String,
    releaseId: String,
    artistId: String,
    artistName: String,
    durationMs: Long,
    playCount: Int,
    likeCount: Int,
    repostCount: Int,
    hasLyrics: Boolean,
    placeInRelease: Int? = null
): Track {
    val artist = Artist(
        id = artistId,
        name = artistName,
        about = null,
        genres = listOf("Electronic"),
        imagesUrl = listOf(),
        statistics = null,
        releases = emptyList()
    )

    val artistOnTrack = Track.ArtistOnTrack(
        artist = artist,
        artistType = Track.ArtistType.Primary
    )

    val statistics = Statistics(playCount, likeCount, repostCount)

    val lyrics = if (hasLyrics) {
        Track.Lyrics(
            plainText = "These are mock lyrics for $name\nLine 1\nLine 2\nLine 3",
            syncedText = mapOf(
                0L to "[00:00] Intro",
                30000L to "[00:30] Verse 1",
                90000L to "[01:30] Chorus"
            ),
            provider = "MockLyricsProvider"
        )
    } else null

    val metadata = Track.Metadata(
        bpm = 120 + Random.nextInt(40),
        format = "mp3",
        bitrate = 320,
        sampleRate = 44100
    )

    val additionalData = Track.AdditionalData(
        fullTitle = "$artistName - $name",
        descriptionMarkdown = "Mock track description for $name",
        descriptionPreviewPlainText = "Mock preview",
        videoShotUrl = "https://example.com/videos/$id.jpg",
        producers = listOf(artistName),
        writers = listOf(artistName),
        tags = listOf("electronic", "synthwave"),
        credits = mapOf("producer" to listOf(artistName)),
        recordingLocation = "Mock Studio",
        textLanguage = "en"
    )

    return Track(
        id = id,
        releaseId = releaseId,
        name = name,
        coverArtUrl = "https://example.com/tracks/$id.jpg",
        audioUrl = "https://example.com/audio/$id.mp3",
        durationMs = durationMs,
        isExplicit = false,
        placeInRelease = placeInRelease,
        genres = listOf("Electronic", "Synthwave"),
        metadata = metadata,
        statistics = statistics,
        hasLyrics = hasLyrics,
        lyrics = lyrics,
        additionalData = additionalData,
        artists = listOf(artistOnTrack),
        release = null
    )
}