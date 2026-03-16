package com.inwave.domain.entity

data class SocialMedia(
    val name: String,
    val link: String,
    val imageUrl: String
)

data class Artist (
    val id: String,
    val name: String,
    val about: String = "",
    val imagesUrl: List<String> = listOf(),
    val listeningInMonth: Int = 0,
    val likes: Int = 0,
    val socialMedia: List<SocialMedia> = listOf()
)