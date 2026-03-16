package com.inwave.backend.data.map

import com.inwave.backend.db.entity.ReleaseAdditionalDataEntity
import com.inwave.backend.db.entity.ReleaseEntity
import com.inwave.backend.db.entity.ReleaseStatisticsEntity
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Statistics
import kotlinx.datetime.toJavaLocalDate
import org.jetbrains.exposed.v1.core.Transaction

context(_: Transaction)
fun ReleaseEntity.toDomain(): Release {
    return Release(
        id = id.value.toString(),
        name = name,
        coverArtUrl = coverArtUrl,
        releaseDate = releaseDate?.toJavaLocalDate(),
        tracks = fetchTracks().map { it.track.toDomain(releaseId = id.value.toString()) },
        artists = artists.map { it.toDomain() },
        genres = genres.map { it.name }.toList(),
        statistics = fetchStatistics()?.toDomain(),
        additionalData = fetchAdditionalData()?.toDomain()
    )
}

fun ReleaseStatisticsEntity.toDomain() =
    Statistics(
        playCount = playCount.toInt(),
        likeCount = likeCount,
        repostCount = repostCount
    )

fun ReleaseAdditionalDataEntity.toDomain() =
    Release.AdditionalData(
        fullTitle = fullTitle,
        descriptionMarkdown = descriptionMd,
        descriptionPreviewPlainText = descriptionPreviewPlain,
        label = label,
        tags = tags ?: listOf(),
        credits = credits
    )