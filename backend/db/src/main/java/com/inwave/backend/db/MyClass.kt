package com.inwave.backend.db

import com.inwave.backend.db.table.TrackAdditionalDataTable
import com.inwave.backend.db.table.TrackLyricsTable
import com.inwave.backend.db.table.TrackMetadataTable
import com.inwave.backend.db.table.TrackStatisticsTable
import com.inwave.backend.db.table.TrackTable
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object Tasks : Table("tasks") {
    val id = integer("id").autoIncrement()
    val title = varchar("name", 32)
    val description = varchar("description", 256)
    val isCompleted = bool("completed").default(false)
}

data class TrackDAO(
    val id: Int,
    val name: String,
    val createAt: LocalDateTime
)

fun main() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/test",
        "org.postgresql.Driver",
        "postgres",
        ""
    )

    transaction {
        SchemaUtils.create(
            TrackTable, TrackMetadataTable, TrackStatisticsTable, TrackAdditionalDataTable, TrackLyricsTable
        )

        TrackTable.selectAll().map {
            TrackDAO(it[TrackTable.id].value, it[TrackTable.name], it[TrackTable.createdAt])
        }.joinToString("\n").let {
            println(it)
        }
    }
}