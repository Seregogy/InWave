package com.inwave.domain.entity

data class Album (
    val id: String,
    val name: String,
    val imageUrl: String,
    val artists: List<Artist>,
    val likes: String,
    val listening: String,
    val releaseDate: Long,
    val genre: String,
    val label: String,
)