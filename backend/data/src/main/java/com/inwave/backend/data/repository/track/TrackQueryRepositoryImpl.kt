package com.inwave.backend.data.repository.track

import com.inwave.backend.data.map.toDomain
import com.inwave.backend.data.repository.catchingTransaction
import com.inwave.backend.db.entity.TrackEntity
import com.inwave.backend.db.table.TrackTable
import com.inwave.domain.entity.Track
import com.inwave.domain.entity.Track.Lyrics
import com.inwave.domain.repository.query.TrackQueryRepository
import org.jetbrains.exposed.v1.core.Random
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll

class TrackQueryRepositoryImpl(
    private val db: Database
) : TrackQueryRepository {
    override suspend fun getTrack(id: String): Result<Track> = catchingTransaction(db) {
        TrackEntity.findById(id.toInt())!!.toDomain()
    }

    override suspend fun getTracks(ids: List<String>): Result<List<Track>> = catchingTransaction(db) {
        ids.map { EntityID(it.toInt(), TrackTable) }.let { entityIDS ->
            TrackEntity.forEntityIds(entityIDS).map { it.toDomain() }
        }
    }

    override suspend fun getAllTracks(
        page: Int,
        size: Int
    ): Result<List<Track>> = catchingTransaction(db) {
        TrackTable.selectAll()
            .drop(page * size)
            .take(size).map {
                TrackEntity.wrapRow(it)
                    .toDomain()
            }
    }


    override suspend fun getRandomTrack(): Result<Track> = catchingTransaction(db) {
        TrackTable.selectAll()
            .orderBy(Random())
            .limit(1)
            .first().let {
                TrackEntity.wrapRow(it).toDomain()
            }
    }


    override suspend fun getRandomTrackId(): Result<String> = catchingTransaction(db) {
        TrackTable.select(TrackTable.id)
            .orderBy(Random())
            .limit(1)
            .first()[TrackTable.id].value.toString()
    }

    override suspend fun getTrackLyrics(id: String): Result<Lyrics> = catchingTransaction(db) {
        TrackEntity.findById(id.toInt())!!
            .fetchLyrics()!!
            .toDomain()
    }


    override suspend fun getTrackWithLyrics(id: String): Result<Track> = catchingTransaction(db) {
        TrackEntity.findById(id.toInt())!!.toDomain()
    }

    override suspend fun toggleLike(id: String): Result<Boolean> {
        TODO("User entity not implemented yet")
    }

    override suspend fun searchTracks(
        query: String,
        limit: Int
    ): Result<List<Track>> = catchingTransaction(db) {
        val escaped = query.replace("%", "\\%")
            .replace("_", "\\_")

        TrackEntity.find {
            TrackTable.name like "%$escaped%"
        }.limit(limit).map {
            it.toDomain()
        }
    }
}