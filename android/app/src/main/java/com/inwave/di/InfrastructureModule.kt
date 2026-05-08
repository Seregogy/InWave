package com.inwave.di

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InfrastructureModule {
    @Provides
    @Singleton
    fun providesKtorClient(): HttpClient {
        return HttpClient(OkHttp) {
            install(HttpCache)

            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                        isLenient = true
                    }
                )
            }

            install(HttpRequestRetry) {
                maxRetries = 3
                delayMillis { retry ->
                    request
                    Log.d("API", "resending ${request.url} #$retry")

                    retry * 3000L
                }
            }

            defaultRequest {
                url {
                    host = "158.160.212.225:8080/api"
                    protocol = URLProtocol.HTTP
                }
                //header("Authorization", "Bearer $accessToken")
            }

            expectSuccess = false

            HttpResponseValidator {
                handleResponseExceptionWithRequest { cause, request ->
                    Log.e("API", "${request.url}, ${cause.message}")
                }
            }
        }
    }
}