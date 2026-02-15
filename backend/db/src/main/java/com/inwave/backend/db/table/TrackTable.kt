package com.inwave.backend.db.table

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.date
import org.jetbrains.exposed.v1.datetime.datetime
import org.jetbrains.exposed.v1.json.jsonb

object TrackTable : IntIdTable() {
    val name = text("name").index("idx_track_name")
    val durationMs = long("duration_ms").nullable()
    val positionInAlbum = integer("position_in_album").nullable()
    val isExplicit = bool("is_explicit").default(false)
    val hasLyrics = bool("has_lyrics").default(false)

    val coverArtUrl = text("cover_art_url").nullable()
    val previewStartMs = integer("preview_start_ms").default(0)
    val previewDurationMs = integer("preview_duration_ms").default(30000)

    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
    val releaseDate = date("release_date").nullable()
}

object TrackMetadataTable : IntIdTable() {
    val trackId = reference("track_id", TrackTable)

    val bpm = integer("bpm").nullable()
    val fileFormat = varchar("file_format", 10).nullable()
    val bitrate = integer("bitrate").nullable()
    val sampleRate = integer("sample_rate").nullable()
}

object TrackStatisticsTable : IntIdTable() {
    val trackId = reference("track_id", TrackTable)

    val playCount = long("play_count").default(0)
    val likeCount = integer("like_count").default(0)
    val repostCount = integer("repost_count").default(0)
    val skipCount = integer("skip_count").default(0)
}

object TrackLyricsTable : IntIdTable() {
    val trackId = reference("track_id", TrackTable)

    val plainText = text("plain_text").nullable()
    val syncedText = text("synced_text").nullable()
}

object TrackAdditionalData : IntIdTable() {
    val trackId = reference("track_id", TrackTable)

    val fullTitle = text("full_title").nullable()
    val descriptionMd = text("description_md").nullable()
    val descriptionPreviewPlain = varchar("description_preview_plain", 256).nullable()
    val producers = array<String>("producers").nullable()
    val writers = array<String>("writers").nullable()
    val tags = array<String>("tags").nullable()
    val recordingLocation = text("recording_location").nullable()
    val textLanguage = varchar("text_language", 32).nullable()

    val credits = jsonb<Map<String, List<String>>>("credits", Json { ignoreUnknownKeys = true })
}