package com.inwave.backend.db.migration

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

private data class TrackRelease(
    val id: String,
    val name: String,
    val releaseName: String
) {
    override fun hashCode(): Int {
        return name.hashCode() + releaseName.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return this.hashCode() == other.hashCode()
    }
}

fun migration3(oldDb: Database, newDb: Database) {
    val tracksAndReleasesFromOldBase = transaction(oldDb) {
        TrackEntity.all().map {
            TrackRelease(
                it.id.toString(),
                it.name,
                it.album.name
            )
        }
    }

    val tracksAndReleasesFromNewBase = transaction(newDb) {
        com.inwave.backend.db.entity.ReleaseEntity.all().flatMap {
            it.fetchTracks().map { trackOnRelease ->
                TrackRelease(
                    trackOnRelease.track.id.toString(),
                    trackOnRelease.track.name,
                    it.name
                )
            }
        }
    }

    BufferedWriter(FileWriter(File("database-id-matches.txt"))).use {
        for (trackAndReleaseFromNewBase in tracksAndReleasesFromNewBase) {
            for (trackAndReleaseFromOldBase in tracksAndReleasesFromOldBase) {
                if (trackAndReleaseFromNewBase == trackAndReleaseFromOldBase) {
                    println("$trackAndReleaseFromNewBase: $trackAndReleaseFromOldBase")
                    it.write("${trackAndReleaseFromNewBase.id}:${trackAndReleaseFromOldBase.id}")
                    it.newLine()

                    break
                }
            }
        }
    }
}