package com.inwave.domain.entity

data class Artist (
    val id: String,
    val name: String,
    val about: String?,
    val genres: List<String>,
    val imagesUrl: List<String>,
    val statistics: Statistics?,
    val releases: List<Release>
)