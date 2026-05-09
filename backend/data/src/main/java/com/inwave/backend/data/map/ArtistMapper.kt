package com.inwave.backend.data.map

import com.inwave.backend.db.entity.ArtistEntity
import com.inwave.backend.db.entity.ArtistStatisticsEntity
import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Statistics
import org.jetbrains.exposed.v1.core.Transaction

context(_: Transaction)
fun ArtistEntity.toDomain(
    releasesLimit: Int = 0
): Artist {
    return Artist(
        id = id.value.toString(),
        name = name,
        about = about,
        genres = genres.map { it.name }.toList(),
        imagesUrl = avatarUrls ?: listOf(),
        statistics = fetchStatistics()?.toDomain(),
        releases = releases.limit(releasesLimit).map { it.toDomain() }
    )
}

fun ArtistStatisticsEntity.toDomain(): Statistics =
    Statistics(
        playCount = playCount.toInt(),
        likeCount = likeCount,
        repostCount = repostCount
    )