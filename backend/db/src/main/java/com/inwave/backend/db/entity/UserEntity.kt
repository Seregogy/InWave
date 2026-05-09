package com.inwave.backend.db.entity

import com.inwave.backend.db.table.UserTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(UserTable)

    val name by UserTable.name
    val passwordHash by UserTable.passwordHash

    val likedTracks by UserTable.likedTracks
    val likedReleases by UserTable.likedReleases

    val createdAt by UserTable.createdAt
    val updatedAt by UserTable.updatedAt
}