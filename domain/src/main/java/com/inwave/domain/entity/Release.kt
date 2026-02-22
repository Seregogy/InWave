package com.inwave.domain.entity

data class Release(
    val id: String,
    val name: String,
    val coverArtUrl: String?,
    val releaseDate: Long,
    val artists: List<Artist>,
    val genres: List<String>,
    val statistics: Statistics?,
    val additionalData: AdditionalData?
) {
    data class AdditionalData(
        val fullTitle: String?,
        val descriptionMarkdown: String?,
        val descriptionPreviewPlainText: String?,
        val label: String?,
        val tags: List<String>,
        val credits: Map<String, List<String>>
    )
}