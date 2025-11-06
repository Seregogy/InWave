package com.inwave.domain.entity

data class Lyrics(
    val plainText : String,
    val syncedText : Map<Long, String>,
    val provider : String
)

data class Track(
    val id : String,
    val name : String,
    val imageUrl : String,
    val indexInAlbum : Int,
    val duration : Int,
    val lyrics : Lyrics,
    val listening : String,
    val audioUrl : String,
    val album : String
)