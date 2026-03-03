package com.inwave.backend.db.migration

import com.inwave.backend.db.entity.ReleaseEntity
import com.inwave.backend.db.entity.TrackLyricsEntity
import com.inwave.backend.db.table.ArtistStatisticsTable
import com.inwave.backend.db.table.ReleaseStatisticsTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.plus
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

fun migration2(db: Database) {
    transaction(db) {
        var entity: TrackLyricsEntity? = null
        TrackLyricsEntity.all().forEach {
            if (entity?.track?.id == it.track.id)
                it.delete()
            else if (it.plainText == null && it.syncedText == null && it.provider == null)
                it.delete()
            else if (it.plainText?.isEmpty() ?: true && it.syncedText?.isEmpty() ?: true && it.provider == null)
                it.delete()
            else
                entity = it
        }

        ReleaseEntity.all().forEach { releaseEntity ->
            println(releaseEntity.name)
            val releasePlayCount = releaseEntity.fetchTracks().sumOf { trackOnRelease ->
                println("\t${trackOnRelease.track.name}")
                trackOnRelease.track.fetchStatistics()?.playCount ?: 0
            }

            ReleaseStatisticsTable.update({ ReleaseStatisticsTable.id eq releaseEntity.id }) {
                it.update(playCount, playCount + releasePlayCount)
            }
        }

        com.inwave.backend.db.entity.ArtistEntity.all().forEach { artistEntity ->
            println(artistEntity.name)
            val artistPlayCount = artistEntity.releases.sumOf { releaseEntity ->
                println("\t${releaseEntity.name}")
                releaseEntity.fetchStatistics()?.playCount ?: 0
            }

            ArtistStatisticsTable.update({ ArtistStatisticsTable.artistId eq artistEntity.id }) {
                it.update(playCount, playCount + artistPlayCount)
            }
        }
    }
}