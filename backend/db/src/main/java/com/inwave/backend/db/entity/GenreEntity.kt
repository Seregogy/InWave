package com.inwave.backend.db.entity

import com.inwave.backend.db.table.ArtistGenreTable
import com.inwave.backend.db.table.GenreTable
import com.inwave.backend.db.table.ReleaseGenreTable
import com.inwave.backend.db.table.TrackGenreTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

class GenreEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GenreEntity>(GenreTable)

    val name by GenreTable.name
    val description by GenreTable.description

    val releases by ReleaseEntity via ReleaseGenreTable
    val tracks by TrackEntity via TrackGenreTable
    val artists by ArtistEntity via ArtistGenreTable
}