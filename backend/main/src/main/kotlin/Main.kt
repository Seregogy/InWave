package com.inwave.backend

import com.inwave.backend.di.databaseModule
import com.inwave.backend.di.envModule
import com.inwave.backend.di.repositoryModule
import com.inwave.backend.di.useCaseModule
import com.inwave.backend.api.v1.artists.getTopArtists
import com.inwave.backend.api.v1.tracks.getRandomTrack
import com.inwave.domain.usecase.artist.query.GetTopArtistsUseCase
import com.inwave.domain.usecase.track.query.GetRandomTrackUseCase
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun main() {
    embeddedServer(Netty, port = 8000) {
        setup()

        routing {
            getRandomTrack(inject<GetRandomTrackUseCase>().value)
            getTopArtists(inject<GetTopArtistsUseCase>().value)
        }
    }.start(wait = true)
}

fun Application.setup() {
    install(Koin) {
        modules(useCaseModule, repositoryModule, envModule, databaseModule)
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        })
    }
}