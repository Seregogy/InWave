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
    val duration : Long,
    val lyrics : Lyrics?,
    val listening : Int,
    val audioUrl : String,
    val album : Album
)