package com.inwave.backend.data.repository.artist

import com.inwave.backend.data.repository.catchingTransaction
import com.inwave.backend.db.entity.ArtistEntity
import com.inwave.backend.db.entity.GenreEntity
import com.inwave.backend.db.entity.ReleaseEntity
import com.inwave.backend.db.table.ArtistGenreTable
import com.inwave.backend.db.table.ArtistTable
import com.inwave.backend.db.table.GenreTable
import com.inwave.backend.db.table.ReleaseTable
import com.inwave.domain.entity.Artist
import com.inwave.domain.repository.command.server.ArtistCommandServerRepository
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.minus
import org.jetbrains.exposed.v1.core.plus
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update

class ArtistCommandRepositoryImpl(
    private val db: Database
) : ArtistCommandServerRepository {
    override suspend fun toggleLikeToTrack(userId: String, resourceId: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun toggleLikeToRelease(
        userId: String,
        releaseId: String
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun create(artist: Artist): Result<String> = catchingTransaction(db) {
        val artistEntity = ArtistEntity.new {
            name = artist.name
            avatarUrls = artist.imagesUrl
            about = artist.about
        }

        artist.genres.forEach { genre ->
            artistEntity.addGenre(
                findOrCreateGenre(genre)
            )
        }

        artistEntity.id.value.toString()
    }

    override suspend fun addRelease(artistId: String, releaseId: String): Result<Unit> = catchingTransaction(db) {
        findArtistById(artistId).addRelease(
            findReleaseById(releaseId)
        )
    }

    override suspend fun addAvatar(artistId: String, avatarUrl: String): Result<Unit> = catchingTransaction(db) {
        val id = artistId.toIntOrNull() ?: error("Invalid artist ID format")

        ArtistTable.update({ ArtistTable.id eq id }) {
            it.update(avatarUrls, avatarUrls + listOf(avatarUrl))
        }
    }

    override suspend fun removeAvatar(artistId: String, avatarUrl: String): Result<Unit> = catchingTransaction(db) {
        val id = artistId.toIntOrNull() ?: error("Invalid artist ID format")

        ArtistTable.update({ ArtistTable.id eq id }) {
            it.update(avatarUrls, avatarUrls - listOf(avatarUrl))
        }
    }

    override suspend fun editAbout(artistId: String, about: String): Result<Unit> = catchingTransaction(db) {
        val id = artistId.toIntOrNull() ?: error("Invalid artist ID format")

        ArtistTable.update({ ArtistTable.id eq id }) {
            it[ArtistTable.about] = about
        }
    }

    override suspend fun addGenre(artistId: String, genre: String): Result<Unit> = catchingTransaction(db) {
        findArtistById(artistId).addGenre(
            findOrCreateGenre(genre)
        )
    }

    override suspend fun removeGenre(artistId: String, genre: String): Result<Unit> = catchingTransaction(db) {
        val genreId = (GenreTable.select(GenreTable.id).where {
            GenreTable.name eq genre
        }.firstOrNull() ?: return@catchingTransaction).get(GenreTable.id)

        val parsedArtistId = artistId.toIntOrNull() ?: error("Invalid artist ID format")

        ArtistGenreTable.deleteWhere {
            (ArtistGenreTable.artistId eq parsedArtistId) and (ArtistGenreTable.genreId eq genreId)
        }
    }

    context(_: Transaction)
    private fun findArtistById(id: String): ArtistEntity {
        val intId = id.toIntOrNull() ?: error("Invalid artist ID format")

        return ArtistEntity.find { ArtistTable.id eq intId }.firstOrNull()
            ?: error("Artist not found with id: $id")
    }

    context(_: Transaction)
    private fun findReleaseById(id: String): ReleaseEntity {
        val intId = id.toIntOrNull() ?: error("Invalid release ID format")

        return ReleaseEntity.find { ReleaseTable.id eq intId }.firstOrNull()
            ?: error("Release not found with id: $id")
    }

    context(_: Transaction)
    private fun findOrCreateGenre(name: String): GenreEntity =
        findGenre(name) ?: GenreEntity.new {
            this.name = name
            this.description = ""
        }

    private fun findGenre(name: String): GenreEntity? =
        GenreEntity.find { GenreTable.name eq name }.firstOrNull()
}