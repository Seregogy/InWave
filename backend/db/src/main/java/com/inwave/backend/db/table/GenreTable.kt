package com.inwave.backend.db.table

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object GenreTable : IntIdTable() {
    val name = text("name")
}