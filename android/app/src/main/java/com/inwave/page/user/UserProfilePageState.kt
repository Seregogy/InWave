// android/app/src/main/java/com/inwave/page/user/UserProfilePageState.kt
package com.inwave.page.user

import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track
import com.inwave.domain.entity.User

sealed class UserProfilePageState {
    object Idle : UserProfilePageState()
    object Loading : UserProfilePageState()
    data class Success(
        val user: User,
        val likedTracks: List<Track>,
        val likedReleases: List<Release>,
    ) : UserProfilePageState()
    data class Error(val exception: Throwable) : UserProfilePageState()
}