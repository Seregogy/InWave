package com.inwave.backend.api.v1.artists

import com.inwave.api.dto.map.toArtistSummaryDto
import com.inwave.backend.map.toGetTopArtistsResponse
import com.inwave.domain.usecase.artist.query.GetTopArtistsUseCase
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable

@Serializable
data class GetTopArtistsResponse(
    val name: String,
    val about: String,
    val playCount: Long
)

fun Route.getTopArtists(
    getTopArtists: GetTopArtistsUseCase
) {
    get("/top") {
        getTopArtists(
            call.queryParameters["limit"]?.toIntOrNull() ?: 10
        ).onSuccess { domainArtists ->
            call.respond(domainArtists.map {
                it.toArtistSummaryDto()
            })
        }.onFailure {
            call.respond(mapOf("error" to it.message))
        }
    }
}