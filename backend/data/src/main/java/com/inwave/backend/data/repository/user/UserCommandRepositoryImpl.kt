package com.inwave.backend.data.repository.user

import com.inwave.backend.data.repository.catchingTransaction
import com.inwave.backend.db.table.UserTable
import com.inwave.domain.entity.User
import com.inwave.domain.repository.command.UserCommandRepository
import com.inwave.domain.service.JWTTokenService
import com.inwave.domain.service.PasswordCryptographyService
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.time.ZoneOffset
import java.util.Date

class UserCommandRepositoryImpl(
    private val db: Database,
    private val tokenService: JWTTokenService,
    private val cryptographyService: PasswordCryptographyService
) : UserCommandRepository {

    override suspend fun register(
        userName: String,
        password: String
    ): Result<User.Token> = catchingTransaction(db) {
        val id = UserTable.insertAndGetId {
            it[UserTable.name] = userName
            it[UserTable.passwordHash] = cryptographyService.hashPassword(password)
        }

        tokenService.generateToken(id.toString())
            .toUserToken()
    }

    override suspend fun login(
        userName: String,
        password: String
    ): Result<User.Token> = catchingTransaction(db) {
        val user = UserTable
            .selectAll()
            .where { UserTable.name eq userName }
            .first()

        require(
            cryptographyService
                .validatePassword(password, user[UserTable.passwordHash])
        ) { "Invalid password" }

        tokenService.generateToken(user[UserTable.id].toString())
            .toUserToken()
    }

    private fun Pair<String, Date>.toUserToken(): User.Token {
        return User.Token(
            first,
            second.toInstant()
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime()
        )
    }
}
