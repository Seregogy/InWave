package com.inwave.domain.entity

data class Track(
    val id: String,
    val releaseId: String?,
    val name: String,
    val coverArtUrl: String?,
    val audioUrl: String,
    val durationMs: Long?,
    val isExplicit: Boolean,
    val placeInRelease: Int?,
    val genres: List<String>,

    val metadata: Metadata?,
    val statistics: Statistics?,
    val hasLyrics: Boolean,
    val lyrics: Lyrics?,
    val additionalData: AdditionalData?,

    val artists: List<ArtistOnTrack>
) {
    enum class ArtistType {
        Primary, Featured, Remixer
    }

    data class ArtistOnTrack(
        val artist: Artist,
        val artistType: ArtistType
    )

    data class Metadata(
        val bpm: Int?,
        val format: String?,
        val bitrate: Int?,
        val sampleRate: Int?
    )

    data class Lyrics(
        val plainText: String?,
        val syncedText: Map<Long, String>?,
        val provider: String?
    )

    data class AdditionalData(
        val fullTitle: String?,
        val descriptionMarkdown: String?,
        val descriptionPreviewPlainText: String?,
        val videoShotUrl: String?,
        val producers: List<String>,
        val writers: List<String>,
        val tags: List<String>,
        val credits: Map<String, List<String>>,
        val recordingLocation: String?,
        val textLanguage: String?
    )
}