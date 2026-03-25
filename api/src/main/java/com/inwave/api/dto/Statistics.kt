package com.inwave.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class StatisticsDto(
    val playCount: Int,
    val likeCount: Int,
    val repostCount: Int
)