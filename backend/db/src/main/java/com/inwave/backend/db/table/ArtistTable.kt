package com.inwave.backend.db.table

import org.jetbrains.exposed.v1.core.dao.id.CompositeIdTable
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

enum class ArtistOnTrackType {
    Primary, Featured, Remixer
}

object ArtistTable : IntIdTable() {
    val name = text("name").index("idx_artist_name")

    val avatarUrls = array<String>("avatar_urls").nullable()
    val about = text("about").nullable()
}

object ArtistStatisticsTable : IntIdTable() {
    val artistId = reference("artist_id", ArtistTable)

    val playCount = long("play_count").default(0)
    val likeCount = integer("like_count").default(0)
    val repostCount = integer("repost_count").default(0)
}

object ArtistGenreTable : CompositeIdTable() {
    val artistId = reference("artist_id", ArtistTable)
    val genreId = reference("genre_id", GenreTable)

    override val primaryKey = PrimaryKey(artistId, genreId)
}

object ArtistReleaseTable : CompositeIdTable() {
    val artistId = reference("artist_id", ArtistTable)
    val releaseId = reference("release_id", ReleaseTable)

    override val primaryKey = PrimaryKey(artistId, releaseId)
}

object ArtistTrackTable : CompositeIdTable() {
    val artistId = reference("artist_id", ArtistTable)
    val trackId = reference("track_id", TrackTable)
    val artistType = enumeration("artist_type", ArtistOnTrackType::class)

    override val primaryKey = PrimaryKey(trackId, artistId)
}

object ArtistLegacyTableId : IntIdTable() {
    val artistId = reference("artist_id", ArtistTable)
    val legacyUuid = text("uuid_Legacy_artist_id")
}