package com.inwave.backend.db.table

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.CompositeIdTable
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime
import org.jetbrains.exposed.v1.json.jsonb

object TrackTable : IntIdTable() {
    val name = text("name").index("idx_track_name")
    val durationMs = long("duration_ms").default(0)
    val isExplicit = bool("is_explicit").default(false)
    val hasLyrics = bool("has_lyrics").default(false)

    val coverArtUrl = text("cover_art_url").nullable()
    val previewStartMs = integer("preview_start_ms").default(0)
    val previewDurationMs = integer("preview_duration_ms").default(30000)

    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
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
}

object TrackLyricsTable : IntIdTable() {
    val trackId = reference("track_id", TrackTable)

    val plainText = text("plain_text").nullable()
    val syncedText = jsonb<Map<Long, String>>("synced_text", Json).nullable()
    val provider = text("provider").nullable()
}

object TrackAdditionalDataTable : IntIdTable() {
    val trackId = reference("track_id", TrackTable)

    val fullTitle = text("full_title").nullable()
    val descriptionMd = text("description_md").nullable()
    val descriptionPreviewPlain = varchar("description_preview_plain", 256).nullable()
    val videoShotUrl = text("video_shot_url").nullable()
    val producers = array<String>("producers").nullable()
    val writers = array<String>("writers").nullable()
    val tags = array<String>("tags").nullable()
    val recordingLocation = text("recording_location").nullable()
    val textLanguage = varchar("text_language", 32).nullable()

    val credits = jsonb<Map<String, List<String>>>("credits", Json { ignoreUnknownKeys = true })
}

object TrackReleaseTable : CompositeIdTable() {
    val trackId = reference("track_id", TrackTable)

    val releaseId = reference("release_id", ReleaseTable)
    val positionInRelease = integer("position_in_release").nullable()
    val discNumber = integer("disc_number").default(1)

    override val primaryKey = PrimaryKey(trackId, releaseId)
}

object TrackGenreTable : CompositeIdTable() {
    val trackId = reference("track_id", TrackTable)
    val genreId = reference("genre_id", GenreTable)
    val weight = float("weight").nullable()

    override val primaryKey = PrimaryKey(trackId, genreId)
}

object TrackLegacyTableId : IntIdTable() {
    val trackId = reference("track_id", TrackTable)
    val legacyUuid = text("uuid_Legacy_track_id")
}