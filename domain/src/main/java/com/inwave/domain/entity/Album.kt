package com.inwave.domain.entity

data class Album (
    val id: String,
    val name: String,
    val imageUrl: String,
    val artists: List<Artist>,
    val likes: Int,
    val listening: Int,
    val releaseDate: Long,
    val genre: String,
    val label: String,
)