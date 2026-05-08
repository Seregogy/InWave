package com.inwave.backend

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.inwave.backend.api.audio.staticContent
import com.inwave.backend.api.v1.artists.getArtist
import com.inwave.backend.api.v1.artists.getTopArtists
import com.inwave.backend.api.v1.artists.releases.getArtistAlbums
import com.inwave.backend.api.v1.artists.releases.getArtistLatestRelease
import com.inwave.backend.api.v1.artists.releases.getArtistReleases
import com.inwave.backend.api.v1.artists.releases.getArtistSingles
import com.inwave.backend.api.v1.artists.tracks.getArtistTopTracks
import com.inwave.backend.api.v1.releases.getRelease
import com.inwave.backend.api.v1.releases.getReleaseTracks
import com.inwave.backend.api.v1.tracks.getRandomTrack
import com.inwave.backend.api.v1.tracks.getRandomTrackId
import com.inwave.backend.api.v1.tracks.getTrack
import com.inwave.backend.api.v1.users.getUser
import com.inwave.backend.api.v1.users.loginUser
import com.inwave.backend.api.v1.users.registerUser
import com.inwave.backend.di.databaseModule
import com.inwave.backend.di.envModule
import com.inwave.backend.di.repositoryModule
import com.inwave.backend.di.serviceModule
import com.inwave.backend.di.useCaseModule
import com.inwave.backend.service.cryptography.JWTTokenServiceImpl
import com.inwave.backend.service.cryptography.PasswordCryptographyServiceBCrypt
import com.inwave.domain.repository.command.UserCommandRepository
import com.inwave.domain.repository.query.UserQueryRepository
import com.inwave.domain.service.JWTTokenService
import com.inwave.domain.service.PasswordCryptographyService
import com.inwave.domain.service.TrackAudioProviderService
import com.inwave.domain.usecase.artist.query.GetArtistAlbumsUseCase
import com.inwave.domain.usecase.artist.query.GetArtistLastReleaseUseCase
import com.inwave.domain.usecase.artist.query.GetArtistReleasesUseCase
import com.inwave.domain.usecase.artist.query.GetArtistSinglesUseCase
import com.inwave.domain.usecase.artist.query.GetArtistTopTracksUseCase
import com.inwave.domain.usecase.artist.query.GetArtistUseCase
import com.inwave.domain.usecase.artist.query.GetTopArtistsUseCase
import com.inwave.domain.usecase.release.query.GetReleaseTracksUseCase
import com.inwave.domain.usecase.release.query.GetReleaseUseCase
import com.inwave.domain.usecase.track.query.GetRandomTrackUseCase
import com.inwave.domain.usecase.track.query.GetTrackUseCase
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.partialcontent.PartialContent
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun main() {
    embeddedServer(
        factory = Netty,
        port = System.getenv()["PORT"]?.toInt() ?: 8080
    ) {
        setupKoin()
        setupPlugins(
            inject<UserQueryRepository>().value,
            inject<Dotenv>().value
        )

        routing {
            route("/api/v1/") {
                route("/tracks") {
                    getRandomTrack(inject<GetRandomTrackUseCase>().value)
                    getRandomTrackId(inject<GetRandomTrackUseCase>().value)

                    getTrack(
                        inject<GetTrackUseCase>().value,
                        inject<TrackAudioProviderService>().value
                    )
                }

                route("/releases") {
                    getRelease(inject<GetReleaseUseCase>().value)
                    getReleaseTracks(inject<GetReleaseTracksUseCase>().value)
                }

                route("/artists") {
                    getTopArtists(inject<GetTopArtistsUseCase>().value)

                    getArtist(inject<GetArtistUseCase>().value)
                    getArtistTopTracks(inject<GetArtistTopTracksUseCase>().value)

                    getArtistSingles(inject<GetArtistSinglesUseCase>().value)
                    getArtistAlbums(inject<GetArtistAlbumsUseCase>().value)
                    getArtistReleases(inject<GetArtistReleasesUseCase>().value)

                    getArtistLatestRelease(inject<GetArtistLastReleaseUseCase>().value)
                }

                route("/users") {
                    registerUser(inject<UserCommandRepository>().value)
                    loginUser(inject<UserCommandRepository>().value)

                    getUser(inject<UserQueryRepository>().value)
                }
            }
        }
    }.start(wait = true)
}

fun Application.setupKoin() {
    install(Koin) {
        modules(envModule)
        modules(module {
            single<JWTTokenService> {
                val dotenv = get<Dotenv>()

                val verifier = JWT.require(Algorithm.HMAC256(dotenv["JWT_SECRET"]))
                    .withIssuer(dotenv["DOMAIN_URL"])
                    .build()

                JWTTokenServiceImpl(get(), verifier)
            }
            single<PasswordCryptographyService> {
                PasswordCryptographyServiceBCrypt()
            }
        })
        modules(useCaseModule, repositoryModule, databaseModule)
        modules(serviceModule)
    }
}

fun Application.setupPlugins(
    userRepository: UserQueryRepository,
    dotenv: Dotenv
) {
    staticContent()
    install(PartialContent)

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        })
    }

    install(Authentication) {
        jwt("user-auth-jwt") {

            verifier(
                JWT.require(Algorithm.HMAC256(dotenv["JWT_SECRET"]))
                    .withIssuer(dotenv["DOMAIN_URL"])
                    .build()
            )
            validate { credentials ->
                val userId = credentials.payload.getClaim("userId")
                    .asString()

                userRepository.getUser(userId).getOrNull()
                    ?.let { JWTPrincipal(credentials.payload) }
            }
            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("error" to "Token invalid or expired")
                )
            }
        }
    }
}