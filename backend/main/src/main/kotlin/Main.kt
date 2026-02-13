package com.inwave.backend

import com.inwave.backend.data.repository.track.TrackRepositoryTestImpl
import com.inwave.backend.di.repositoryModule
import com.inwave.backend.di.useCaseModule
import com.inwave.backend.route.track.getRandomTrack
import com.inwave.domain.repository.TrackRepository
import com.inwave.domain.usecase.track.GetRandomTrackUseCase
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.di.DependencyKey
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun main() {
    embeddedServer(Netty, port = 8000) {
        setup()

        routing {
            getRandomTrack(inject<GetRandomTrackUseCase>().value)
        }
    }.start(wait = true)
}

fun Application.setup() {
    dependencies {
        provide<TrackRepository> {
            TrackRepositoryTestImpl()
        }
        provide<GetRandomTrackUseCase> {
            GetRandomTrackUseCase(get(DependencyKey<TrackRepository>()))
        }
    }

    install(Koin) {
        modules(useCaseModule,repositoryModule)
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        })
    }
}