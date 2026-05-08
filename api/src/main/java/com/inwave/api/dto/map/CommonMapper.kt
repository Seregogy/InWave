package com.inwave.api.dto.map

import com.inwave.api.dto.StatisticsDto
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Statistics

fun Statistics.toStatisticsDto(): StatisticsDto {
    return StatisticsDto(
        playCount = playCount,
        likeCount = likeCount,
        repostCount = repostCount
    )
}

fun StatisticsDto.toDomain(): Statistics {
    return Statistics(
        playCount = playCount,
        likeCount = likeCount,
        repostCount = repostCount
    )
}

fun Release.determineReleaseType(): String {
    return when {
        this.tracks.size == 1 -> "SINGLE"
        this.tracks.size <= 4 -> "EP"
        else -> "ALBUM"
    }
}