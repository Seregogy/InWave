package com.inwave.backend

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.inwave.backend.api.audio.setupStaticContent
import com.inwave.backend.api.v1.artists.getArtist
import com.inwave.backend.api.v1.artists.getTopArtists
import com.inwave.backend.api.v1.artists.releases.getArtistAlbums
import com.inwave.backend.api.v1.artists.releases.getArtistLatestRelease
import com.inwave.backend.api.v1.artists.releases.getArtistReleases
import com.inwave.backend.api.v1.artists.releases.getArtistSingles
import com.inwave.backend.api.v1.artists.tracks.getArtistTopTracks
import com.inwave.backend.api.v1.releases.getRelease
import com.inwave.backend.api.v1.releases.getReleaseTracks
import com.inwave.backend.api.v1.releases.likeRelease
import com.inwave.backend.api.v1.status
import com.inwave.backend.api.v1.tracks.getRandomTrack
import com.inwave.backend.api.v1.tracks.getRandomTrackId
import com.inwave.backend.api.v1.tracks.getTrack
import com.inwave.backend.api.v1.tracks.likeTrack
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
import com.inwave.domain.repository.query.UserQueryRepository
import com.inwave.domain.service.JWTTokenService
import com.inwave.domain.service.PasswordCryptographyService
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
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin

fun main() {
    embeddedServer(
        factory = Netty,
        port = System.getenv()["PORT"]?.toInt() ?: 8080
    ) {
        setupKoin()
        setupPlugins(get(), get())

        setupStaticContent()
        routing {
            route("/api/v1/") {
                route("/status") {
                    status()
                }

                route("/tracks") {
                    getRandomTrack(get(), get())
                    getRandomTrackId(get())

                    getTrack(get(), get())

                    likeTrack(get())
                }

                route("/releases") {
                    getRelease(get())
                    getReleaseTracks(get(), get())

                    likeRelease(get())
                }

                route("/artists") {
                    getTopArtists(get())

                    getArtist(get())
                    getArtistTopTracks(get())

                    getArtistSingles(get())
                    getArtistAlbums(get())
                    getArtistReleases(get())

                    getArtistLatestRelease(get())
                }

                route("/users") {
                    registerUser(get())
                    loginUser(get())

                    getUser(get())
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