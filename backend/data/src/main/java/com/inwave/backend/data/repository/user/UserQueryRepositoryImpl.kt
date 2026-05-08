package com.inwave.backend.data.repository.user

import com.inwave.backend.data.repository.catchingTransaction
import com.inwave.backend.data.repository.suspendCatchingTransaction
import com.inwave.backend.db.table.UserTable
import com.inwave.domain.entity.User
import com.inwave.domain.repository.query.UserQueryRepository
import com.inwave.domain.service.JWTTokenService
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.selectAll

class UserQueryRepositoryImpl(
    private val db: Database,
    private val tokenService: JWTTokenService
) : UserQueryRepository {
    override suspend fun getUser(userId: String): Result<User> = catchingTransaction(db) {
        return@catchingTransaction UserTable
            .selectAll()
            .where { UserTable.id eq userId.toInt() }
            .first()
            .let {
                User(
                    it[UserTable.id].toString(),
                    it[UserTable.name],
                    avatarUrl = it[UserTable.avatarUrl] ?: "",
                    isAuthenticated = false,
                    likedTracks = it[UserTable.likedTracks]
                        .map { trackId -> trackId.toString() },
                    likedReleases = it[UserTable.likedReleases]
                        .map { releaseId -> releaseId.toString() }
                )
            }
    }

    override suspend fun getUserByToken(token: String): Result<User> = suspendCatchingTransaction(db) {
        getUser(tokenService.extractId(token).getOrThrow())
            .getOrThrow()
            .copy(isAuthenticated = true)
    }
}