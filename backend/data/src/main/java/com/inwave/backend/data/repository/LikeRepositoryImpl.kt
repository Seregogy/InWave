package com.inwave.backend.data.repository

import com.inwave.backend.db.table.ReleaseTable
import com.inwave.backend.db.table.TrackTable
import com.inwave.backend.db.table.UserTable
import com.inwave.domain.repository.command.LikeRepository
import com.inwave.domain.service.JWTTokenService
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update

class LikeRepositoryImpl(
    private val db: Database,
    private val jwtTokenService: JWTTokenService
) : LikeRepository {
    override suspend fun toggleLikeToTrack(
        authToken: String,
        trackId: String
    ): Result<Boolean> = catchingTransaction(db) {
        require(
            TrackTable.select(TrackTable.id).where {
                TrackTable.id eq trackId.toInt()
            }.any()
        ) { "Track not found" }

        val userId = jwtTokenService.extractId(authToken)
            .getOrThrow()
            .toInt()

        val tracks = UserTable.select(UserTable.likedTracks)
            .where { UserTable.id eq userId }
            .first()[UserTable.likedTracks]

        if (tracks.contains(trackId.toInt()))
            return@catchingTransaction true

        UserTable.update({ UserTable.id eq userId }) {
            it[UserTable.likedTracks] = tracks.toMutableList().apply {
                add(trackId.toInt())
            }
        } > 0
    }

    override suspend fun toggleLikeToRelease(
        authToken: String,
        releaseId: String
    ): Result<Boolean> = catchingTransaction(db) {
        require(
            ReleaseTable.select(ReleaseTable.id).where {
                ReleaseTable.id eq releaseId.toInt()
            }.any()
        ) { "Release not found" }

        val userId = jwtTokenService.extractId(authToken)
            .getOrThrow()
            .toInt()

        val releases = UserTable.select(UserTable.likedReleases)
            .where { UserTable.id eq userId }
            .first()[UserTable.likedReleases]

        if (releases.contains(releaseId.toInt()))
            return@catchingTransaction true

        UserTable.update({ UserTable.id eq userId }) {
            it[UserTable.likedReleases] = releases.toMutableList().apply {
                add(releaseId.toInt())
            }
        } > 0
    }
}