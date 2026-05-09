package com.inwave.backend.db.table

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

object UserTable : IntIdTable() {
    val name = text("name").index("idx_user_name")
        .uniqueIndex()
    val passwordHash = text("password_hash")

    val avatarUrl = text("avatar_url").nullable()

    val likedTracks = array<Int>("liked_tracks").default(listOf())
    val likedReleases = array<Int>("liked_releases").default(listOf())

    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}