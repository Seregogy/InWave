package com.invawe.data.repository.artist

import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Statistics
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.query.ArtistQueryRepository
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ArtistQueryRepositoryMock : ArtistQueryRepository {
    private val mockArtists = listOf(
        createPostMalone(),
        createMachineGunKelly(),
        createDrake(),
        createBillieEilish(),
        createTheWeeknd()
    )

    override suspend fun getArtist(artistId: String): Result<Artist> {
        delay(500)
        return try {
            Result.success(mockArtists.first { it.id == artistId })
        } catch (e: NoSuchElementException) {
            Result.failure(IllegalArgumentException("Artist not found: $artistId"))
        }
    }

    override suspend fun getTopArtists(limit: Int): Result<List<Artist>> {
        delay(500)
        // Sort by play count (statistics) and take top N
        val topArtists = mockArtists
            .sortedByDescending { it.statistics?.playCount ?: 0 }
            .take(limit)
        return Result.success(topArtists)
    }

    override suspend fun getArtistReleases(artistId: String): Result<List<Release>> {
        delay(500)
        return try {
            val artist = mockArtists.first { it.id == artistId }
            Result.success(artist.releases)
        } catch (e: NoSuchElementException) {
            Result.failure(IllegalArgumentException("Artist not found: $artistId"))
        }
    }

    override suspend fun getArtistTopTracks(artistId: String, limit: Int): Result<List<Track>> {
        delay(500)
        return try {
            val artist = mockArtists.first { it.id == artistId }
            val allTracks = artist.releases.flatMap { it.tracks }
            val topTracks = allTracks
                .sortedByDescending { it.statistics?.playCount ?: 0 }
                .take(limit)
            Result.success(topTracks)
        } catch (e: NoSuchElementException) {
            Result.failure(IllegalArgumentException("Artist not found: $artistId"))
        }
    }

    override suspend fun getArtistSingles(artistId: String): Result<List<Release>> {
        delay(500)
        return try {
            val artist = mockArtists.first { it.id == artistId }
            // Filter releases that are singles (has "single" in tags or track count = 1)
            val singles = artist.releases.filter { release ->
                release.additionalData?.tags?.contains("single") == true ||
                        release.tracks.size == 1
            }
            Result.success(singles)
        } catch (e: NoSuchElementException) {
            Result.failure(IllegalArgumentException("Artist not found: $artistId"))
        }
    }

    override suspend fun getArtistLastRelease(artistId: String): Result<Pair<Release, Long>> {
        delay(500)
        return try {
            val artist = mockArtists.first { it.id == artistId }
            val releasesWithDates = artist.releases.filter { it.releaseDate != null }

            if (releasesWithDates.isEmpty()) {
                return Result.failure(IllegalStateException("No releases with dates found for artist: $artistId"))
            }

            val lastRelease = releasesWithDates.maxBy { it.releaseDate!! }
            val daysSinceRelease = ChronoUnit.DAYS.between(lastRelease.releaseDate, LocalDate.now())

            Result.success(Pair(lastRelease, daysSinceRelease))
        } catch (e: NoSuchElementException) {
            Result.failure(IllegalArgumentException("Artist not found: $artistId"))
        }
    }

    private fun createPostMalone(): Artist {
        val releases = listOf(
            createStoneyRelease(),
            createTwelveCaratToothacheRelease(),
            createBeerbongsAndBentleysRelease()
        )

        return Artist(
            id = "artist_post_malone",
            name = "Post Malone",
            about = "Austin Richard Post, known professionally as Post Malone, is an American rapper, singer, songwriter, and record producer. He has gained acclaim for his blending of genres including hip hop, pop, R&B, and trap. His stage name was derived from inputting his birth name into a rap name generator.",
            genres = listOf("Hip hop", "Pop", "Rap rock", "Trap", "R&B"),
            imagesUrl = listOf(
                "https://m.media-amazon.com/images/M/MV5BN2VmNDI3OWUtMGEyYS00Njg5LTlkNDUtOTI1MDk5MjdmYmExXkEyXkFqcGc@._V1_FMjpg_UX1000_.jpg",
                "https://hips.hearstapps.com/hmg-prod/images/post-malone-performs-on-day-3-of-outside-lands-festival-news-photo-1724072301.jpg?crop=1.00xw:0.849xh;0,0.0421xh&resize=1200:*",
                "https://cdn-image.zvuk.com/pic?hash=082cae5c-fe1c-415c-95a0-62bee8d5da54&id=3289907&size=large&type=artist"
            ),
            statistics = Statistics(
                playCount = 15_000_000,
                likeCount = 2_500_000,
                repostCount = 850_000
            ),
            releases = releases
        )
    }

    private fun createMachineGunKelly(): Artist {
        val releases = listOf(
            createHotelDiabloRelease(),
            createTicketsToMyDownfallRelease(),
            createMainstreamSelloutRelease()
        )

        return Artist(
            id = "artist_mgk",
            name = "Machine Gun Kelly",
            about = "Colson Baker, known professionally as Machine Gun Kelly (MGK), is an American musician, rapper, and actor. He is noted for his musical versatility, blending hip hop, rock, and pop punk. His stage name is derived from the notorious gangster George 'Machine Gun Kelly' Barnes, reflecting his fast-paced rap style.",
            genres = listOf("Hip hop", "Pop punk", "Rap rock", "Alternative rock"),
            imagesUrl = listOf(
                "https://www.rollingstone.com/wp-content/uploads/2025/08/mgk-on-sinners.jpg",
                "https://i.scdn.co/image/ab6761610000517485e7615a199f8b17fabfcd61"
            ),
            statistics = Statistics(
                playCount = 8_500_000,
                likeCount = 1_200_000,
                repostCount = 420_000
            ),
            releases = releases
        )
    }

    private fun createDrake(): Artist {
        val releases = listOf(
            createHerLossRelease(),
            createCertifiedLoverBoyRelease()
        )

        return Artist(
            id = "artist_drake",
            name = "Drake",
            about = "Aubrey Drake Graham is a Canadian rapper, singer, and songwriter. An influential figure in contemporary popular music, Drake has been credited for popularizing singing and R&B sensibilities in hip hop.",
            genres = listOf("Hip hop", "R&B", "Pop", "Trap"),
            imagesUrl = listOf(
                "https://i.scdn.co/image/ab676161000051744293385d324db8558179afd9",
                "https://s0.rbk.ru/v6_top_pics/media/img/4/77/756609283990774.jpg"
            ),
            statistics = Statistics(
                playCount = 28_000_000,
                likeCount = 4_500_000,
                repostCount = 1_800_000
            ),
            releases = releases
        )
    }

    private fun createBillieEilish(): Artist {
        val releases = listOf(
            createHappierThanEverRelease(),
            createWhenWeAllFallAsleepRelease()
        )

        return Artist(
            id = "artist_billie_eilish",
            name = "Billie Eilish",
            about = "Billie Eilish Pirate Baird O'Connell is an American singer-songwriter. She first gained public attention in 2015 with her debut single 'Ocean Eyes', and has since become one of the most successful artists of her generation.",
            genres = listOf("Pop", "Alternative pop", "Electropop"),
            imagesUrl = listOf(
                "https://upload.wikimedia.org/wikipedia/commons/c/c9/Billie_Eilish_at_Pukkelpop_Festival_-_18_AUGUST_2019_%2801%29_%28cropped%29.jpg",
                "https://sp-ao.shortpixel.ai/client/to_auto,q_glossy,ret_img/https://bnmusic.ru/wp-content/uploads/2019/07/Billie-Elish.jpg"
            ),
            statistics = Statistics(
                playCount = 22_000_000,
                likeCount = 3_800_000,
                repostCount = 1_200_000
            ),
            releases = releases
        )
    }

    private fun createTheWeeknd(): Artist {
        val releases = listOf(
            createAfterHoursRelease(),
            createDawnFMRelease()
        )

        return Artist(
            id = "artist_the_weeknd",
            name = "The Weeknd",
            about = "Abel Makkonen Tesfaye, known professionally as The Weeknd, is a Canadian singer, songwriter, and record producer. Known for his unconventional musical production and artistic reinventions, he is one of the most successful musical acts of all time.",
            genres = listOf("R&B", "Pop", "Synthwave"),
            imagesUrl = listOf(
                "https://upload.wikimedia.org/wikipedia/commons/b/b8/FEQ_July_2018_The_Weeknd_%2844778856382%29_%28cropped%29.jpg",
                "https://static.wikia.nocookie.net/singmovie/images/b/b5/The-Weeknd.jpg/revision/latest?cb=20220808010527"
            ),
            statistics = Statistics(
                playCount = 25_000_000,
                likeCount = 4_200_000,
                repostCount = 1_500_000
            ),
            releases = releases
        )
    }

    // MARK: - Post Malone Releases

    private fun createStoneyRelease(): Release {
        val postMalone = Artist(
            id = "artist_post_malone",
            name = "Post Malone",
            about = "",
            genres = emptyList(),
            imagesUrl = emptyList(),
            statistics = null,
            releases = emptyList()
        )

        val tracks = listOf(
            Track(
                id = "track_congratulations",
                releaseId = "release_stoney",
                name = "Congratulations",
                coverArtUrl = "https://example.com/cover_congratulations.jpg",
                audioUrl = "https://example.com/audio/congratulations.mp3",
                durationMs = 220000,
                isExplicit = true,
                placeInRelease = 1,
                genres = listOf("Hip hop", "Trap"),
                metadata = Track.Metadata(125, "MP3", 320, 44100),
                statistics = Statistics(45_000_000, 2_100_000, 890_000),
                hasLyrics = true,
                lyrics = Track.Lyrics("Plain text lyrics...", null, "Genius"),
                additionalData = Track.AdditionalData(
                    fullTitle = "Congratulations (feat. Quavo) (by Post Malone)",
                    descriptionMarkdown = "The breakout hit from Post Malone's debut album...",
                    descriptionPreviewPlainText = "The breakout hit from Post Malone's debut album...",
                    videoShotUrl = null,
                    producers = listOf("Metro Boomin", "Louis Bell"),
                    writers = listOf("Austin Post", "Quavious Marshall", "Leland Wayne", "Carlton Mays"),
                    tags = listOf("hip hop", "trap", "celebration"),
                    credits = mapOf(),
                    recordingLocation = "Los Angeles, CA",
                    textLanguage = "en"
                ),
                artists = listOf(Track.ArtistOnTrack(postMalone, Track.ArtistType.Primary))
            ),
            Track(
                id = "track_white_iverson",
                releaseId = "release_stoney",
                name = "White Iverson",
                coverArtUrl = "https://example.com/cover_white_iverson.jpg",
                audioUrl = "https://example.com/audio/white_iverson.mp3",
                durationMs = 257000,
                isExplicit = true,
                placeInRelease = 2,
                genres = listOf("Hip hop", "Trap"),
                metadata = Track.Metadata(140, "MP3", 320, 44100),
                statistics = Statistics(78_000_000, 3_500_000, 1_200_000),
                hasLyrics = true,
                lyrics = Track.Lyrics("Plain text lyrics...", null, "Genius"),
                additionalData = Track.AdditionalData(
                    fullTitle = "White Iverson (by Post Malone)",
                    descriptionMarkdown = "Post Malone's debut single that put him on the map...",
                    descriptionPreviewPlainText = "Post Malone's debut single that put him on the map...",
                    videoShotUrl = null,
                    producers = listOf("Post Malone", "Rex Kudo"),
                    writers = listOf("Austin Post"),
                    tags = listOf("hip hop", "trap", "debut"),
                    credits = mapOf(),
                    recordingLocation = "Dallas, TX",
                    textLanguage = "en"
                ),
                artists = listOf(Track.ArtistOnTrack(postMalone, Track.ArtistType.Primary))
            )
        )

        return Release(
            id = "release_stoney",
            name = "Stoney",
            coverArtUrl = "https://images.genius.com/dcf3b834de38c6f2c28f0fe961f8dc80.1000x1000x1.png",
            releaseDate = LocalDate.of(2016, 12, 9),
            tracks = tracks,
            artists = listOf(postMalone),
            genres = listOf("Hip hop", "Pop", "Trap"),
            statistics = Statistics(120_000_000, 8_500_000, 3_200_000),
            additionalData = Release.AdditionalData(
                fullTitle = "Stoney (by Post Malone)",
                descriptionMarkdown = "The debut studio album by Post Malone...",
                descriptionPreviewPlainText = "The debut studio album by Post Malone...",
                label = "Republic Records",
                tags = listOf("album", "debut", "2016", "hip hop"),
                credits = mapOf()
            )
        )
    }

    private fun createTwelveCaratToothacheRelease(): Release {
        val postMalone = Artist(
            id = "artist_post_malone",
            name = "Post Malone",
            about = "",
            genres = emptyList(),
            imagesUrl = emptyList(),
            statistics = null,
            releases = emptyList()
        )

        val tracks = listOf(
            Track(
                id = "track_reputation",
                releaseId = "release_twelve_carat_toothache",
                name = "Reputation",
                coverArtUrl = "https://example.com/cover_reputation.jpg",
                audioUrl = "https://example.com/audio/reputation.mp3",
                durationMs = 246000,
                isExplicit = true,
                placeInRelease = 1,
                genres = listOf("Hip hop", "Pop"),
                metadata = Track.Metadata(150, "MP3", 320, 44100),
                statistics = Statistics(12_000_000, 890_000, 310_000),
                hasLyrics = true,
                lyrics = Track.Lyrics("Plain text lyrics...", null, "Genius"),
                additionalData = Track.AdditionalData(
                    fullTitle = "Reputation (by Post Malone)",
                    descriptionMarkdown = "Opening track from Twelve Carat Toothache...",
                    descriptionPreviewPlainText = "Opening track from Twelve Carat Toothache...",
                    videoShotUrl = null,
                    producers = listOf("Louis Bell", "Frank Dukes"),
                    writers = listOf("Austin Post", "Louis Bell", "Adam Feeney"),
                    tags = listOf("hip hop", "pop", "intro"),
                    credits = mapOf(),
                    recordingLocation = "New York, NY",
                    textLanguage = "en"
                ),
                artists = listOf(Track.ArtistOnTrack(postMalone, Track.ArtistType.Primary))
            )
        )

        return Release(
            id = "release_twelve_carat_toothache",
            name = "Twelve Carat Toothache",
            coverArtUrl = "https://images.genius.com/11934fa629be4cbe4bfc5772d1757bfc.1000x1000x1.png",
            releaseDate = LocalDate.of(2022, 6, 3),
            tracks = tracks,
            artists = listOf(postMalone),
            genres = listOf("Hip hop", "Pop", "Rap rock"),
            statistics = Statistics(45_000_000, 2_800_000, 950_000),
            additionalData = Release.AdditionalData(
                fullTitle = "Twelve Carat Toothache (by Post Malone)",
                descriptionMarkdown = "The fourth studio album by Post Malone...",
                descriptionPreviewPlainText = "The fourth studio album by Post Malone...",
                label = "Republic Records",
                tags = listOf("album", "2022", "hip hop", "pop"),
                credits = mapOf()
            )
        )
    }

    private fun createBeerbongsAndBentleysRelease(): Release {
        val postMalone = Artist(
            id = "artist_post_malone",
            name = "Post Malone",
            about = "",
            genres = emptyList(),
            imagesUrl = emptyList(),
            statistics = null,
            releases = emptyList()
        )

        val tracks = listOf(
            Track(
                id = "track_rockstar",
                releaseId = "release_beerbongs_and_bentleys",
                name = "rockstar",
                coverArtUrl = "https://example.com/cover_rockstar.jpg",
                audioUrl = "https://example.com/audio/rockstar.mp3",
                durationMs = 218000,
                isExplicit = true,
                placeInRelease = 1,
                genres = listOf("Hip hop", "Trap"),
                metadata = Track.Metadata(160, "MP3", 320, 44100),
                statistics = Statistics(150_000_000, 8_200_000, 3_500_000),
                hasLyrics = true,
                lyrics = Track.Lyrics("Plain text lyrics...", null, "Genius"),
                additionalData = Track.AdditionalData(
                    fullTitle = "rockstar (feat. 21 Savage) (by Post Malone)",
                    descriptionMarkdown = "The hit single from beerbongs & bentleys...",
                    descriptionPreviewPlainText = "The hit single from beerbongs & bentleys...",
                    videoShotUrl = null,
                    producers = listOf("Tank God", "Louis Bell"),
                    writers = listOf("Austin Post", "Shéyaa Bin Abraham-Joseph", "Louis Bell", "Olufunmibi Awoshiley"),
                    tags = listOf("hip hop", "trap", "hit"),
                    credits = mapOf(),
                    recordingLocation = "Los Angeles, CA",
                    textLanguage = "en"
                ),
                artists = listOf(Track.ArtistOnTrack(postMalone, Track.ArtistType.Primary))
            )
        )

        return Release(
            id = "release_beerbongs_and_bentleys",
            name = "beerbongs & bentleys",
            coverArtUrl = "https://example.com/beerbongs_bentleys_cover.jpg",
            releaseDate = LocalDate.of(2018, 4, 27),
            tracks = tracks,
            artists = listOf(postMalone),
            genres = listOf("Hip hop", "Pop", "Trap"),
            statistics = Statistics(200_000_000, 12_500_000, 5_800_000),
            additionalData = Release.AdditionalData(
                fullTitle = "beerbongs & bentleys (by Post Malone)",
                descriptionMarkdown = "The second studio album by Post Malone...",
                descriptionPreviewPlainText = "The second studio album by Post Malone...",
                label = "Republic Records",
                tags = listOf("album", "2018", "hip hop", "pop"),
                credits = mapOf()
            )
        )
    }

    // MARK: - Machine Gun Kelly Releases

    private fun createHotelDiabloRelease(): Release {
        val machineGunKelly = Artist(
            id = "artist_mgk",
            name = "Machine Gun Kelly",
            about = "",
            genres = emptyList(),
            imagesUrl = emptyList(),
            statistics = null,
            releases = emptyList()
        )

        val tracks = listOf(
            Track(
                id = "track_floor_13",
                releaseId = "release_hotel_diablo",
                name = "Floor 13",
                coverArtUrl = "https://example.com/cover_floor_13.jpg",
                audioUrl = "https://example.com/audio/floor_13.mp3",
                durationMs = 194000,
                isExplicit = true,
                placeInRelease = 1,
                genres = listOf("Hip hop", "Rap rock"),
                metadata = Track.Metadata(140, "MP3", 320, 44100),
                statistics = Statistics(8_500_000, 520_000, 180_000),
                hasLyrics = true,
                lyrics = Track.Lyrics("Plain text lyrics...", null, "Genius"),
                additionalData = Track.AdditionalData(
                    fullTitle = "Floor 13 (by Machine Gun Kelly)",
                    descriptionMarkdown = "Opening track from Hotel Diablo...",
                    descriptionPreviewPlainText = "Opening track from Hotel Diablo...",
                    videoShotUrl = null,
                    producers = listOf("Rami", "SlimXX"),
                    writers = listOf("Colson Baker", "Rami Eadeh"),
                    tags = listOf("hip hop", "rap rock", "intro"),
                    credits = mapOf(),
                    recordingLocation = "Los Angeles, CA",
                    textLanguage = "en"
                ),
                artists = listOf(Track.ArtistOnTrack(machineGunKelly, Track.ArtistType.Primary))
            )
        )

        return Release(
            id = "release_hotel_diablo",
            name = "Hotel Diablo",
            coverArtUrl = "https://images.genius.com/ed781e8cae245fce215d4cc9d926b332.1000x1000x1.png",
            releaseDate = LocalDate.of(2019, 7, 5),
            tracks = tracks,
            artists = listOf(machineGunKelly),
            genres = listOf("Hip hop", "Alternative rock", "Pop punk"),
            statistics = Statistics(28_000_000, 1_500_000, 620_000),
            additionalData = Release.AdditionalData(
                fullTitle = "Hotel Diablo (by Machine Gun Kelly)",
                descriptionMarkdown = "The fourth studio album by Machine Gun Kelly...",
                descriptionPreviewPlainText = "The fourth studio album by Machine Gun Kelly...",
                label = "Bad Boy, Interscope Records",
                tags = listOf("album", "2019", "hip hop", "alternative"),
                credits = mapOf()
            )
        )
    }

    private fun createTicketsToMyDownfallRelease(): Release {
        val machineGunKelly = Artist(
            id = "artist_mgk",
            name = "Machine Gun Kelly",
            about = "",
            genres = emptyList(),
            imagesUrl = emptyList(),
            statistics = null,
            releases = emptyList()
        )

        val tracks = listOf(
            Track(
                id = "track_bloody_valentine",
                releaseId = "release_tickets_to_my_downfall",
                name = "bloody valentine",
                coverArtUrl = "https://example.com/cover_bloody_valentine.jpg",
                audioUrl = "https://example.com/audio/bloody_valentine.mp3",
                durationMs = 198000,
                isExplicit = true,
                placeInRelease = 1,
                genres = listOf("Pop punk", "Alternative rock"),
                metadata = Track.Metadata(95, "MP3", 320, 44100),
                statistics = Statistics(35_000_000, 2_200_000, 780_000),
                hasLyrics = true,
                lyrics = Track.Lyrics("Plain text lyrics...", null, "Genius"),
                additionalData = Track.AdditionalData(
                    fullTitle = "bloody valentine (by Machine Gun Kelly)",
                    descriptionMarkdown = "Lead single from Tickets To My Downfall...",
                    descriptionPreviewPlainText = "Lead single from Tickets To My Downfall...",
                    videoShotUrl = null,
                    producers = listOf("Travis Barker"),
                    writers = listOf("Colson Baker", "Travis Barker", "Nick Long"),
                    tags = listOf("pop punk", "alternative", "single"),
                    credits = mapOf(),
                    recordingLocation = "Los Angeles, CA",
                    textLanguage = "en"
                ),
                artists = listOf(Track.ArtistOnTrack(machineGunKelly, Track.ArtistType.Primary))
            )
        )

        return Release(
            id = "release_tickets_to_my_downfall",
            name = "Tickets To My Downfall",
            coverArtUrl = "https://example.com/tickets_to_my_downfall_cover.jpg",
            releaseDate = LocalDate.of(2020, 9, 25),
            tracks = tracks,
            artists = listOf(machineGunKelly),
            genres = listOf("Pop punk", "Alternative rock"),
            statistics = Statistics(50_000_000, 3_100_000, 1_250_000),
            additionalData = Release.AdditionalData(
                fullTitle = "Tickets To My Downfall (by Machine Gun Kelly)",
                descriptionMarkdown = "The fifth studio album by Machine Gun Kelly...",
                descriptionPreviewPlainText = "The fifth studio album by Machine Gun Kelly...",
                label = "Bad Boy, Interscope Records",
                tags = listOf("album", "2020", "pop punk", "alternative"),
                credits = mapOf()
            )
        )
    }

    private fun createMainstreamSelloutRelease(): Release {
        val machineGunKelly = Artist(
            id = "artist_mgk",
            name = "Machine Gun Kelly",
            about = "",
            genres = emptyList(),
            imagesUrl = emptyList(),
            statistics = null,
            releases = emptyList()
        )

        val tracks = listOf(
            Track(
                id = "track_emo_girl",
                releaseId = "release_mainstream_sellout",
                name = "emo girl",
                coverArtUrl = "https://example.com/cover_emo_girl.jpg",
                audioUrl = "https://example.com/audio/emo_girl.mp3",
                durationMs = 158000,
                isExplicit = true,
                placeInRelease = 1,
                genres = listOf("Pop punk", "Alternative rock"),
                metadata = Track.Metadata(125, "MP3", 320, 44100),
                statistics = Statistics(15_000_000, 980_000, 320_000),
                hasLyrics = true,
                lyrics = Track.Lyrics("Plain text lyrics...", null, "Genius"),
                additionalData = Track.AdditionalData(
                    fullTitle = "emo girl (feat. Willow) (by Machine Gun Kelly)",
                    descriptionMarkdown = "Lead single from Mainstream Sellout...",
                    descriptionPreviewPlainText = "Lead single from Mainstream Sellout...",
                    videoShotUrl = null,
                    producers = listOf("Travis Barker", "Nick Long"),
                    writers = listOf("Colson Baker", "Willow Smith", "Travis Barker", "Nick Long"),
                    tags = listOf("pop punk", "alternative", "single", "2022"),
                    credits = mapOf(),
                    recordingLocation = "Los Angeles, CA",
                    textLanguage = "en"
                ),
                artists = listOf(Track.ArtistOnTrack(machineGunKelly, Track.ArtistType.Primary))
            )
        )

        return Release(
            id = "release_mainstream_sellout",
            name = "Mainstream Sellout",
            coverArtUrl = "https://example.com/mainstream_sellout_cover.jpg",
            releaseDate = LocalDate.of(2022, 3, 25),
            tracks = tracks,
            artists = listOf(machineGunKelly),
            genres = listOf("Pop punk", "Alternative rock", "Rap rock"),
            statistics = Statistics(30_000_000, 1_900_000, 680_000),
            additionalData = Release.AdditionalData(
                fullTitle = "Mainstream Sellout (by Machine Gun Kelly)",
                descriptionMarkdown = "The sixth studio album by Machine Gun Kelly...",
                descriptionPreviewPlainText = "The sixth studio album by Machine Gun Kelly...",
                label = "Bad Boy, Interscope Records",
                tags = listOf("album", "2022", "pop punk", "alternative"),
                credits = mapOf()
            )
        )
    }

    // MARK: - Drake Releases

    private fun createHerLossRelease(): Release {
        val drake = Artist(
            id = "artist_drake",
            name = "Drake",
            about = "",
            genres = emptyList(),
            imagesUrl = emptyList(),
            statistics = null,
            releases = emptyList()
        )

        val tracks = listOf(
            Track(
                id = "track_rich_flex",
                releaseId = "release_her_loss",
                name = "Rich Flex",
                coverArtUrl = "https://example.com/cover_rich_flex.jpg",
                audioUrl = "https://example.com/audio/rich_flex.mp3",
                durationMs = 239000,
                isExplicit = true,
                placeInRelease = 1,
                genres = listOf("Hip hop", "Trap"),
                metadata = Track.Metadata(142, "MP3", 320, 44100),
                statistics = Statistics(85_000_000, 3_200_000, 1_100_000),
                hasLyrics = true,
                lyrics = Track.Lyrics("Plain text lyrics...", null, "Genius"),
                additionalData = Track.AdditionalData(
                    fullTitle = "Rich Flex (feat. 21 Savage) (by Drake)",
                    descriptionMarkdown = "Lead single from Her Loss...",
                    descriptionPreviewPlainText = "Lead single from Her Loss...",
                    videoShotUrl = null,
                    producers = listOf("Wheezy", "Vinylz"),
                    writers = listOf("Aubrey Graham", "Shéyaa Bin Abraham-Joseph", "Wesley Glass", "Anderson Hernandez"),
                    tags = listOf("hip hop", "trap", "single", "2022"),
                    credits = mapOf(),
                    recordingLocation = "Toronto, Canada",
                    textLanguage = "en"
                ),
                artists = listOf(Track.ArtistOnTrack(drake, Track.ArtistType.Primary))
            )
        )

        return Release(
            id = "release_her_loss",
            name = "Her Loss",
            coverArtUrl = "https://example.com/her_loss_cover.jpg",
            releaseDate = LocalDate.of(2022, 11, 4),
            tracks = tracks,
            artists = listOf(drake),
            genres = listOf("Hip hop", "Trap"),
            statistics = Statistics(110_000_000, 5_200_000, 2_100_000),
            additionalData = Release.AdditionalData(
                fullTitle = "Her Loss (with 21 Savage) (by Drake)",
                descriptionMarkdown = "Collaborative album by Drake and 21 Savage...",
                descriptionPreviewPlainText = "Collaborative album by Drake and 21 Savage...",
                label = "OVO Sound, Republic Records",
                tags = listOf("album", "collaboration", "2022", "hip hop"),
                credits = mapOf()
            )
        )
    }

    private fun createCertifiedLoverBoyRelease(): Release {
        val drake = Artist(
            id = "artist_drake",
            name = "Drake",
            about = "",
            genres = emptyList(),
            imagesUrl = emptyList(),
            statistics = null,
            releases = emptyList()
        )

        val tracks = listOf(
            Track(
                id = "track_way_2_sexy",
                releaseId = "release_certified_lover_boy",
                name = "Way 2 Sexy",
                coverArtUrl = "https://example.com/cover_way_2_sexy.jpg",
                audioUrl = "https://example.com/audio/way_2_sexy.mp3",
                durationMs = 278000,
                isExplicit = true,
                placeInRelease = 1,
                genres = listOf("Hip hop", "R&B"),
                metadata = Track.Metadata(130, "MP3", 320, 44100),
                statistics = Statistics(92_000_000, 4_100_000, 1_800_000),
                hasLyrics = true,
                lyrics = Track.Lyrics("Plain text lyrics...", null, "Genius"),
                additionalData = Track.AdditionalData(
                    fullTitle = "Way 2 Sexy (feat. Future & Young Thug) (by Drake)",
                    descriptionMarkdown = "Hit single from Certified Lover Boy...",
                    descriptionPreviewPlainText = "Hit single from Certified Lover Boy...",
                    videoShotUrl = null,
                    producers = listOf("ATL Jacob", "TM88"),
                    writers = listOf("Aubrey Graham", "Nayvadius Wilburn", "Jeffery Williams", "Jacob Canady", "Bryan Simmons"),
                    tags = listOf("hip hop", "r&b", "single", "2021"),
                    credits = mapOf(),
                    recordingLocation = "Toronto, Canada",
                    textLanguage = "en"
                ),
                artists = listOf(Track.ArtistOnTrack(drake, Track.ArtistType.Primary))
            )
        )

        return Release(
            id = "release_certified_lover_boy",
            name = "Certified Lover Boy",
            coverArtUrl = "https://example.com/certified_lover_boy_cover.jpg",
            releaseDate = LocalDate.of(2021, 9, 3),
            tracks = tracks,
            artists = listOf(drake),
            genres = listOf("Hip hop", "R&B", "Pop"),
            statistics = Statistics(180_000_000, 9_500_000, 4_200_000),
            additionalData = Release.AdditionalData(
                fullTitle = "Certified Lover Boy (by Drake)",
                descriptionMarkdown = "The sixth studio album by Drake...",
                descriptionPreviewPlainText = "The sixth studio album by Drake...",
                label = "OVO Sound, Republic Records",
                tags = listOf("album", "2021", "hip hop", "r&b"),
                credits = mapOf()
            )
        )
    }

    // MARK: - Billie Eilish Releases

    private fun createHappierThanEverRelease(): Release {
        val billieEilish = Artist(
            id = "artist_billie_eilish",
            name = "Billie Eilish",
            about = "",
            genres = emptyList(),
            imagesUrl = emptyList(),
            statistics = null,
            releases = emptyList()
        )

        val tracks = listOf(
            Track(
                id = "track_happier_than_ever",
                releaseId = "release_happier_than_ever",
                name = "Happier Than Ever",
                coverArtUrl = "https://example.com/cover_happier_than_ever.jpg",
                audioUrl = "https://example.com/audio/happier_than_ever.mp3",
                durationMs = 298000,
                isExplicit = true,
                placeInRelease = 1,
                genres = listOf("Pop", "Alternative"),
                metadata = Track.Metadata(120, "MP3", 320, 44100),
                statistics = Statistics(110_000_000, 6_800_000, 2_500_000),
                hasLyrics = true,
                lyrics = Track.Lyrics("Plain text lyrics...", null, "Genius"),
                additionalData = Track.AdditionalData(
                    fullTitle = "Happier Than Ever (by Billie Eilish)",
                    descriptionMarkdown = "Title track from Happier Than Ever...",
                    descriptionPreviewPlainText = "Title track from Happier Than Ever...",
                    videoShotUrl = null,
                    producers = listOf("Finneas O'Connell"),
                    writers = listOf("Billie Eilish", "Finneas O'Connell"),
                    tags = listOf("pop", "alternative", "single", "2021"),
                    credits = mapOf(),
                    recordingLocation = "Los Angeles, CA",
                    textLanguage = "en"
                ),
                artists = listOf(Track.ArtistOnTrack(billieEilish, Track.ArtistType.Primary))
            )
        )

        return Release(
            id = "release_happier_than_ever",
            name = "Happier Than Ever",
            coverArtUrl = "https://example.com/happier_than_ever_cover.jpg",
            releaseDate = LocalDate.of(2021, 7, 30),
            tracks = tracks,
            artists = listOf(billieEilish),
            genres = listOf("Pop", "Alternative pop", "Electropop"),
            statistics = Statistics(150_000_000, 9_200_000, 3_800_000),
            additionalData = Release.AdditionalData(
                fullTitle = "Happier Than Ever (by Billie Eilish)",
                descriptionMarkdown = "The second studio album by Billie Eilish...",
                descriptionPreviewPlainText = "The second studio album by Billie Eilish...",
                label = "Darkroom, Interscope Records",
                tags = listOf("album", "2021", "pop", "alternative"),
                credits = mapOf()
            )
        )
    }

    private fun createWhenWeAllFallAsleepRelease(): Release {
        val billieEilish = Artist(
            id = "artist_billie_eilish",
            name = "Billie Eilish",
            about = "",
            genres = emptyList(),
            imagesUrl = emptyList(),
            statistics = null,
            releases = emptyList()
        )

        val tracks = listOf(
            Track(
                id = "track_bad_guy",
                releaseId = "release_when_we_all_fall_asleep",
                name = "bad guy",
                coverArtUrl = "https://example.com/cover_bad_guy.jpg",
                audioUrl = "https://example.com/audio/bad_guy.mp3",
                durationMs = 194000,
                isExplicit = true,
                placeInRelease = 1,
                genres = listOf("Pop", "Electropop"),
                metadata = Track.Metadata(135, "MP3", 320, 44100),
                statistics = Statistics(220_000_000, 12_500_000, 5_800_000),
                hasLyrics = true,
                lyrics = Track.Lyrics("Plain text lyrics...", null, "Genius"),
                additionalData = Track.AdditionalData(
                    fullTitle = "bad guy (by Billie Eilish)",
                    descriptionMarkdown = "Breakthrough single from Billie Eilish...",
                    descriptionPreviewPlainText = "Breakthrough single from Billie Eilish...",
                    videoShotUrl = null,
                    producers = listOf("Finneas O'Connell"),
                    writers = listOf("Billie Eilish", "Finneas O'Connell"),
                    tags = listOf("pop", "electropop", "single", "2019"),
                    credits = mapOf(),
                    recordingLocation = "Los Angeles, CA",
                    textLanguage = "en"
                ),
                artists = listOf(Track.ArtistOnTrack(billieEilish, Track.ArtistType.Primary))
            )
        )

        return Release(
            id = "release_when_we_all_fall_asleep",
            name = "When We All Fall Asleep, Where Do We Go?",
            coverArtUrl = "https://example.com/when_we_all_fall_asleep_cover.jpg",
            releaseDate = LocalDate.of(2019, 3, 29),
            tracks = tracks,
            artists = listOf(billieEilish),
            genres = listOf("Pop", "Electropop", "Alternative"),
            statistics = Statistics(280_000_000, 15_800_000, 7_200_000),
            additionalData = Release.AdditionalData(
                fullTitle = "When We All Fall Asleep, Where Do We Go? (by Billie Eilish)",
                descriptionMarkdown = "The debut studio album by Billie Eilish...",
                descriptionPreviewPlainText = "The debut studio album by Billie Eilish...",
                label = "Darkroom, Interscope Records",
                tags = listOf("album", "debut", "2019", "pop"),
                credits = mapOf()
            )
        )
    }

    // MARK: - The Weeknd Releases

    private fun createAfterHoursRelease(): Release {
        val theWeeknd = Artist(
            id = "artist_the_weeknd",
            name = "The Weeknd",
            about = "",
            genres = emptyList(),
            imagesUrl = emptyList(),
            statistics = null,
            releases = emptyList()
        )

        val tracks = listOf(
            Track(
                id = "track_blinding_lights",
                releaseId = "release_after_hours",
                name = "Blinding Lights",
                coverArtUrl = "https://example.com/cover_blinding_lights.jpg",
                audioUrl = "https://example.com/audio/blinding_lights.mp3",
                durationMs = 200000,
                isExplicit = false,
                placeInRelease = 1,
                genres = listOf("Synthwave", "Pop"),
                metadata = Track.Metadata(171, "MP3", 320, 44100),
                statistics = Statistics(300_000_000, 18_500_000, 8_200_000),
                hasLyrics = true,
                lyrics = Track.Lyrics("Plain text lyrics...", null, "Genius"),
                additionalData = Track.AdditionalData(
                    fullTitle = "Blinding Lights (by The Weeknd)",
                    descriptionMarkdown = "Record-breaking hit from After Hours...",
                    descriptionPreviewPlainText = "Record-breaking hit from After Hours...",
                    videoShotUrl = null,
                    producers = listOf("Max Martin", "Oscar Holter", "The Weeknd"),
                    writers = listOf("Abel Tesfaye", "Max Martin", "Oscar Holter", "Ahmad Balshe"),
                    tags = listOf("synthwave", "pop", "single", "2020"),
                    credits = mapOf(),
                    recordingLocation = "Los Angeles, CA",
                    textLanguage = "en"
                ),
                artists = listOf(Track.ArtistOnTrack(theWeeknd, Track.ArtistType.Primary))
            )
        )

        return Release(
            id = "release_after_hours",
            name = "After Hours",
            coverArtUrl = "https://example.com/after_hours_cover.jpg",
            releaseDate = LocalDate.of(2020, 3, 20),
            tracks = tracks,
            artists = listOf(theWeeknd),
            genres = listOf("R&B", "Pop", "Synthwave"),
            statistics = Statistics(350_000_000, 20_100_000, 9_500_000),
            additionalData = Release.AdditionalData(
                fullTitle = "After Hours (by The Weeknd)",
                descriptionMarkdown = "The fourth studio album by The Weeknd...",
                descriptionPreviewPlainText = "The fourth studio album by The Weeknd...",
                label = "XO, Republic Records",
                tags = listOf("album", "2020", "r&b", "synthwave"),
                credits = mapOf()
            )
        )
    }

    private fun createDawnFMRelease(): Release {
        val theWeeknd = Artist(
            id = "artist_the_weeknd",
            name = "The Weeknd",
            about = "",
            genres = emptyList(),
            imagesUrl = emptyList(),
            statistics = null,
            releases = emptyList()
        )

        val tracks = listOf(
            Track(
                id = "track_sacrifice",
                releaseId = "release_dawn_fm",
                name = "Sacrifice",
                coverArtUrl = "https://example.com/cover_sacrifice.jpg",
                audioUrl = "https://example.com/audio/sacrifice.mp3",
                durationMs = 188000,
                isExplicit = true,
                placeInRelease = 1,
                genres = listOf("Synthwave", "Pop"),
                metadata = Track.Metadata(117, "MP3", 320, 44100),
                statistics = Statistics(85_000_000, 4_200_000, 1_500_000),
                hasLyrics = true,
                lyrics = Track.Lyrics("Plain text lyrics...", null, "Genius"),
                additionalData = Track.AdditionalData(
                    fullTitle = "Sacrifice (by The Weeknd)",
                    descriptionMarkdown = "Lead single from Dawn FM...",
                    descriptionPreviewPlainText = "Lead single from Dawn FM...",
                    videoShotUrl = null,
                    producers = listOf("Max Martin", "Oscar Holter", "The Weeknd"),
                    writers = listOf("Abel Tesfaye", "Max Martin", "Oscar Holter", "Ahmad Balshe"),
                    tags = listOf("synthwave", "pop", "single", "2022"),
                    credits = mapOf(),
                    recordingLocation = "Los Angeles, CA",
                    textLanguage = "en"
                ),
                artists = listOf(Track.ArtistOnTrack(theWeeknd, Track.ArtistType.Primary))
            )
        )

        return Release(
            id = "release_dawn_fm",
            name = "Dawn FM",
            coverArtUrl = "https://example.com/dawn_fm_cover.jpg",
            releaseDate = LocalDate.of(2022, 1, 7),
            tracks = tracks,
            artists = listOf(theWeeknd),
            genres = listOf("Synthwave", "Pop", "R&B"),
            statistics = Statistics(120_000_000, 6_500_000, 2_800_000),
            additionalData = Release.AdditionalData(
                fullTitle = "Dawn FM (by The Weeknd)",
                descriptionMarkdown = "The fifth studio album by The Weeknd...",
                descriptionPreviewPlainText = "The fifth studio album by The Weeknd...",
                label = "XO, Republic Records",
                tags = listOf("album", "2022", "synthwave", "pop"),
                credits = mapOf()
            )
        )
    }
}