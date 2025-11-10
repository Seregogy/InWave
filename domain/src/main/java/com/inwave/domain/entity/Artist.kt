package com.inwave.domain.entity

data class Artist (
    val id : String,
    val name : String,
    val imageUrl : String? = null,
    val genres : List<String> = emptyList(),
    val followers : Int = 0,
    val popularity : Int = 0
)