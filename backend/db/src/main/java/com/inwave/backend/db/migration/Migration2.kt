package com.inwave.backend.db.migration

import com.inwave.backend.db.entity.TrackLyricsEntity
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

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
    }
}