package com.inwave.backend.db

import com.inwave.backend.db.entity.TrackEntity
import com.inwave.backend.db.table.ArtistGenresTable
import com.inwave.backend.db.table.ArtistReleasesTable
import com.inwave.backend.db.table.ArtistStatisticsTable
import com.inwave.backend.db.table.ArtistTable
import com.inwave.backend.db.table.ArtistTracksTable
import com.inwave.backend.db.table.ReleaseAdditionalDataTable
import com.inwave.backend.db.table.ReleaseGenreTable
import com.inwave.backend.db.table.ReleaseStatisticsTable
import com.inwave.backend.db.table.ReleaseTable
import com.inwave.backend.db.table.TrackAdditionalDataTable
import com.inwave.backend.db.table.TrackGenreTable
import com.inwave.backend.db.table.TrackLyricsTable
import com.inwave.backend.db.table.TrackMetadataTable
import com.inwave.backend.db.table.TrackReleasesTable
import com.inwave.backend.db.table.TrackStatisticsTable
import com.inwave.backend.db.table.TrackTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils

fun main() {
    connectDb()
    configureDb()

    checkTrack()
}
fun connectDb() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/test",
        "org.postgresql.Driver",
        "postgres",
        ""
    )
}

fun configureDb() {
    transaction {
        SchemaUtils.create(
            TrackTable, TrackMetadataTable, TrackStatisticsTable, TrackLyricsTable,
            TrackAdditionalDataTable, TrackReleasesTable, TrackGenreTable
        )

        SchemaUtils.create(
            ReleaseTable, ReleaseStatisticsTable, ReleaseAdditionalDataTable, ReleaseGenreTable
        )

        SchemaUtils.create(
            ArtistTable, ArtistStatisticsTable, ArtistGenresTable, ArtistReleasesTable,
            ArtistTracksTable
        )
    }

    transaction {
        MigrationUtils.statementsRequiredForDatabaseMigration(
            TrackTable, TrackMetadataTable, TrackStatisticsTable, TrackLyricsTable,
            TrackAdditionalDataTable, TrackReleasesTable, TrackGenreTable, ReleaseTable,
            ReleaseStatisticsTable, ReleaseAdditionalDataTable, ReleaseGenreTable, ArtistTable,
            ArtistStatisticsTable, ArtistGenresTable, ArtistReleasesTable, ArtistTracksTable
        ).forEach { println(it) }
    }
}

fun checkTrack() {
    transaction {
        val track = TrackEntity.find { TrackTable.name eq "Glass House" }.firstOrNull()

        if (track != null) {
            println("Track: ${track.name}")
            println("Metadata: ${track.fetchMetadata()?.bpm} BPM")
            println("Listening: ${track.fetchStatistics()?.playCount}")
        } else {
            println("Track NF")
        }
    }
}