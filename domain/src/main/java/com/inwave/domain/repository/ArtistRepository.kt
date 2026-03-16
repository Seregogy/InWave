package com.inwave.domain.repository

import com.inwave.domain.entity.Album
import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Track

interface ArtistRepository {
    suspend fun getArtist(artistId: String): Result<Artist>
    suspend fun getTopArtists(limit: Int = 10): Result<List<Artist>>
    suspend fun getArtistAlbums(artistId: String): Result<List<Album>>
    suspend fun getArtistTopTracks(artistId: String, limit: Int = 9): Result<List<Track>>
}