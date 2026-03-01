package com.inwave.backend.db.table

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.dao.id.CompositeIdTable
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.date
import org.jetbrains.exposed.v1.datetime.datetime
import org.jetbrains.exposed.v1.json.jsonb

enum class ReleaseType {
    Album, Single, EP
}

object ReleaseTable : IntIdTable() {
    val name = text("name").index("idx_release_name")
    val releaseType = enumeration("release_type", ReleaseType::class).default(ReleaseType.Single)
    val coverArtUrl = text("cover_art_url").nullable()

    val totalTracks = integer("total_tracks").default(0)
    val totalDurationMs = long("total_duration_ms").default(0)

    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
    val releaseDate = date("release_date").nullable()
}

object ReleaseStatisticsTable : IntIdTable() {
    val releaseId = reference("release_id", ReleaseTable)

    val playCount = long("play_count").default(0)
    val likeCount = integer("like_count").default(0)
    val repostCount = integer("repost_count").default(0)
}

object ReleaseAdditionalDataTable : IntIdTable() {
    val releaseId = reference("release_id", ReleaseTable)

    val fullTitle = text("full_title").nullable()
    val descriptionMd = text("description_md").nullable()
    val descriptionPreviewPlain = text("description_preview_plain").nullable()
    val label = text("label").nullable()

    val tags = array<String>("tags").nullable()
    val credits = jsonb<Map<String, List<String>>>("credits", Json { ignoreUnknownKeys = true })
}

object ReleaseGenreTable : CompositeIdTable() {
    val releaseId = reference("release_id", ReleaseTable)
    val genreId = reference("genre_id", GenreTable)

    override val primaryKey = PrimaryKey(releaseId, genreId)
}

object ReleaseLegacyTableId : IntIdTable() {
    val releaseId = reference("release_id", ReleaseTable)
    val legacyUuid = text("uuid_Legacy_release_id")
}