package com.inwave.backend.db.migration

import com.inwave.backend.db.entity.ReleaseEntity
import com.inwave.backend.db.entity.TrackLyricsEntity
import com.inwave.backend.db.table.ArtistLegacyTableId
import com.inwave.backend.db.table.ArtistReleasesTable
import com.inwave.backend.db.table.ArtistStatisticsTable
import com.inwave.backend.db.table.ReleaseLegacyTableId
import com.inwave.backend.db.table.ReleaseStatisticsTable
import com.inwave.backend.db.table.TrackLegacyTableId
import com.inwave.backend.db.table.TrackReleaseTable
import com.inwave.backend.db.table.TrackStatisticsTable
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.plus
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.util.UUID


class AlbumEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<AlbumEntity>(AlbumsTable)

    var name by AlbumsTable.name
    var likes by AlbumsTable.likes
    var listening by AlbumsTable.listening
    var releaseDate by AlbumsTable.releaseDate
    var imageUrl by AlbumsTable.imageUrl

    val tracks by TrackEntity.Companion referrersOn TracksTable.albumId
    var artists by ArtistEntity.Companion via ArtistAlbumsTable
}

object AlbumsTable : UUIDTable("ALBUMS_TABLE") {
    val name = text("name")
    val likes = integer("likes").default(0)
    val listening = integer("listening").default(0)
    val releaseDate = long("release_date").default(0)
    val imageUrl = text("image_url").nullable()
}

class ArtistEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ArtistEntity>(ArtistsTable)

    var name by ArtistsTable.name
    var about by ArtistsTable.about
    var listeningInMonth by ArtistsTable.listeningInMonth
    var likes by ArtistsTable.likes

    val socialMedias by SocialMediaEntity referrersOn SocialMediasTable.artist
    var albums by AlbumEntity via ArtistAlbumsTable
    val imagesUrl by ArtistImageEntity referrersOn ImagesUrlTable.artist

    override fun toString(): String {
        return "$name ($id), likes = $likes, streaming = $listeningInMonth"
    }
}

class SocialMediaEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<SocialMediaEntity>(SocialMediasTable)

    var socialMediaName by SocialMediasTable.socialMediaName
    var link by SocialMediasTable.link

    var artist by ArtistEntity referencedOn SocialMediasTable.artist
}

class ArtistImageEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ArtistImageEntity>(ImagesUrlTable)

    var imageUrl by ImagesUrlTable.imageUrl
    var artist by ArtistEntity referencedOn ImagesUrlTable.artist
}

object ArtistsTable : UUIDTable("ARTISTS") {
    val name = text("name")
    val about = text("about").nullable()
    val listeningInMonth = integer("listeningInMonth").default(0)
    val likes = integer("likes").default(0)
}

object ImagesUrlTable : UUIDTable("IMAGES_URLS_TABLE") {
    val artist = reference("artist", ArtistsTable)
    val imageUrl = text("imageUrl")
}

object SocialMediasTable : UUIDTable("SOCIAL_MEDIAS_TABLE") {
    val artist = reference("artist", ArtistsTable)
    val socialMediaName = text("social_media_name")
    val link = text("link")
}

object ArtistAlbumsTable : IntIdTable("ARTIST_ALBUMS_TABLE") {
    val artist = reference("artist", ArtistsTable)
    val album = reference("album", AlbumsTable)
}

class TrackEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<TrackEntity>(TracksTable)

    var name by TracksTable.name
    var durationSeconds by TracksTable.durationSeconds
    var indexInAlbum by TracksTable.indexInAlbum
    var listening by TracksTable.listening
    var isExplicit by TracksTable.isExplicit

    val lyrics by LyricsEntity referrersOn LyricsTable.track
    var album by AlbumEntity.Companion referencedOn TracksTable.albumId
    var artists by ArtistEntity.Companion via ArtistsOnTrackTable

    //var additional by AdditionalTrackDataEntity optionalReferencedOn TracksTable.additional
}

class LyricsEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<LyricsEntity>(LyricsTable)

    var plainText by LyricsTable.plainText
    var syncedText by LyricsTable.syncedText

    var track by TrackEntity referencedOn LyricsTable.track
}

object TracksTable : UUIDTable("TRACKS_TABLE") {
    val albumId = reference("albumId", AlbumsTable)
    val name = text("name")
    val durationSeconds  = integer("durationSeconds ").default(0)
    val indexInAlbum = integer("index_in_album").default(0)
    val listening = integer("listening").default(0)
    val isExplicit = bool("isExplicit").default(false)

    //val additional = optReference("additional", AdditionalTrackDataTable).uniqueIndex()
}

object ArtistsOnTrackTable : UUIDTable("ARTISTS_ON_TRACK_TABLE") {
    val track = reference("track", TracksTable)
    val artist = reference("artist", ArtistsTable)
}

object GenresTable : UUIDTable("TRACKS_GENRES") {
    var genreName = text("genre_name")
}

object TracksToGenreTable : UUIDTable("TRACKS_TO_GENRE_TABLE") {
    val track = reference("track", TracksTable)
    val genre = reference("genre", GenresTable)
}

object LyricsTable : UUIDTable("LYRICS_TABLE") {
    val plainText = text("plain_text").nullable()
    val syncedText = text("synced_text").nullable()

    val track = reference("track", TracksTable).uniqueIndex()
}

data class AdditionalTrackData(
    val fullTitle: String?,
    val description: String?,
    val producers: List<String>?,
    val writers: List<String>?,

    // здесь нужна ссылка из entity
    val tags: List<String>?,
    val credits: Map<String, List<String>>?,
    val language: String?,
    val recordingLocation: String?,
    val releaseData: Long?
)

object AdditionalTrackDataTable : UUIDTable("ADDITIONAL_TRACK_DATA_TABLE") {
    val fullTitle = text("full_title").nullable()
    val description = text("description").nullable()
    val producers = array<String>("producers")
    val writers = array<String>("writers")
    val releaseDate = long("release_date")
    val recordingLocation = text("recording_location")
}

class AdditionalTrackDataEntity(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<AdditionalTrackDataEntity>(AdditionalTrackDataTable)

    //val track by TrackEntity backReferencedOn TracksTable.additional
}



fun migration1(
    oldDb: Database,
    newDb: Database
) {
    runCatching {
        val oldTracks = transaction(oldDb) { TrackEntity.all().toList() }
        val oldAlbums = transaction(oldDb) { AlbumEntity.all().toList() }
        val oldArtists = transaction(oldDb) { ArtistEntity.all().toList() }

        transaction(newDb) {
            oldTracks.forEach { oldTrack ->
                com.inwave.backend.db.entity.TrackEntity.new {
                    name = oldTrack.name
                    durationMs = (oldTrack.durationSeconds * 1000).toLong()
                    isExplicit = oldTrack.isExplicit

                    TrackStatisticsTable.update({ TrackStatisticsTable.trackId eq this.id }) {
                        it.update(playCount, playCount + oldTrack.listening.toLong())
                    }

                    val oldLyrics = transaction(oldDb) {
                        oldTrack.lyrics.firstOrNull()
                    }

                    val self = this@new
                    updateLyrics(TrackLyricsEntity.new {
                        track = self

                        oldLyrics?.syncedText?.let {
                            syncedText = parseSyncedLyrics(it)
                        }
                        plainText = oldLyrics?.plainText
                    })

                    TrackLegacyTableId.insert {
                        it[trackId] = this@new.id
                        it[legacyUuid] = oldTrack.id.toString()
                    }
                }
            }

            oldAlbums.forEach { oldAlbum ->
                ReleaseEntity.new {
                    name = oldAlbum.name
                    coverArtUrl = oldAlbum.imageUrl

                    releaseDate = LocalDate.fromEpochDays(oldAlbum.releaseDate / 86400)

                    ReleaseStatisticsTable.update({ ReleaseStatisticsTable.releaseId eq this.id }) {
                        it.update(likeCount, likeCount + oldAlbum.likes)
                        it.update(playCount, playCount + (oldAlbum.listening).toLong())
                    }


                    transaction(oldDb) { oldAlbum.tracks.toList() }.map { oldTrack ->
                        oldTrack to TrackLegacyTableId.select(TrackLegacyTableId.trackId).where {
                            TrackLegacyTableId.legacyUuid eq oldTrack.id.toString()
                        }.first()[TrackLegacyTableId.trackId]
                    }.forEach { (oldTrack, newId) ->
                        TrackReleaseTable.insert {
                            it[trackId] = newId
                            it[releaseId] = this@new.id
                            it[positionInRelease] = oldTrack.indexInAlbum
                        }
                    }

                    ReleaseLegacyTableId.insert {
                        it[releaseId] = this@new.id
                        it[legacyUuid] = oldAlbum.id.toString()
                    }
                }
            }

            oldArtists.forEach { oldArtist ->
                com.inwave.backend.db.entity.ArtistEntity.new {
                    name = oldArtist.name
                    about = oldArtist.about
                    avatarUrls = transaction(oldDb) {
                        oldArtist.imagesUrl.map { it.imageUrl }
                    }

                    ArtistStatisticsTable.update({ ArtistStatisticsTable.artistId eq this.id }) {
                        it.update(likeCount, likeCount + oldArtist.likes)
                    }

                    transaction(oldDb) { oldArtist.albums.toList() }.map { oldAlbum ->
                        ReleaseLegacyTableId.select(ReleaseLegacyTableId.releaseId).where {
                            ReleaseLegacyTableId.legacyUuid eq oldAlbum.id.toString()
                        }.first()[ReleaseLegacyTableId.releaseId]
                    }.forEach { newId ->
                        ArtistReleasesTable.insert {
                            it[artistId] = this@new.id
                            it[releaseId] = newId
                        }
                    }

                    ArtistLegacyTableId.insert {
                        it[artistId] = this@new.id
                        it[legacyUuid] = oldArtist.id.toString()
                    }
                }
            }
        }
    }.onFailure { println("EXCEPTION: ${it.stackTrace.joinToString("\n")}") }
}

fun parseSyncedLyrics(syncedLyrics: String): Map<Long, String> {
    return Regex("""\[(\d{2}:\d{2}\.\d{2})](.+)\n""").findAll(syncedLyrics).map {
        val parsedMs = it.groups[1]?.value?.split(":", ".")?.map { it.toLong() }?.let {
            it[0].minToMs() + it[1].secToMs() + it[2].partOfSecToMs()
        }
        (parsedMs ?: 0) to (it.groups[2]?.value?.trim() ?: "")
    }.toMap()
}

fun Int.minToSec(): Int {
    return this * 60
}

fun Long.minToMs(): Long {
    return this * 60L.secToMs()
}

fun Long.secToMs(): Long {
    return this * 1000L
}

fun Long.partOfSecToMs(): Long {
    return this * 10L
}