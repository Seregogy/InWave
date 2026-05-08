// android/app/src/main/java/com/inwave/page/user/UserProfilePageState.kt
package com.inwave.page.user

import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Playlist
import com.inwave.domain.entity.Track
import com.inwave.domain.entity.User

sealed class UserProfilePageState {
    object Idle : UserProfilePageState()
    object Loading : UserProfilePageState()
    data class Success(
        val user: User,
        val playlists: List<Playlist> = emptyList(),
        val topArtists: List<Artist> = emptyList(),
        val recentlyPlayed: List<Track> = emptyList()
    ) : UserProfilePageState()
    data class Error(val exception: Throwable) : UserProfilePageState()
}