package domain.repository

import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track

interface AlbumRepository {
    suspend fun getAlbum(albumId: String): Result<Release>
    suspend fun getAlbumTracks(albumId: String): Result<List<Track>>
    suspend fun getArtistAlbums(artistId: String): Result<List<Release>>
    suspend fun getArtistSingles(artistId: String): Result<List<Release>>
    suspend fun getArtistReleases(artistId: String): Result<List<Release>>
    suspend fun getArtistLastRelease(artistId: String): Result<Pair<Release, Long>>
}