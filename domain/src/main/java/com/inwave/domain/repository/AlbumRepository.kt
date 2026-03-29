package domain.repository

import com.inwave.domain.entity.Album
import com.inwave.domain.entity.Track

interface AlbumRepository {
    suspend fun getAlbum(albumId: String): Result<Album>
    suspend fun getAlbumTracks(albumId: String): Result<List<Track>>
    suspend fun getArtistAlbums(artistId: String): Result<List<Album>>
    suspend fun getArtistSingles(artistId: String): Result<List<Album>>
    suspend fun getArtistReleases(artistId: String): Result<List<Album>>
    suspend fun getArtistLastRelease(artistId: String): Result<Pair<Album, Long>>
}