package com.inwave.domain.entity

import java.time.LocalDateTime

data class User(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val isAuthenticated: Boolean,
    val likedTracks: List<String>,
    val likedReleases: List<String>
) {
    data class Token(
        val token: String,
        val expiredAt: LocalDateTime
    )
}