package com.inwave.backend.service.track.additionaldata

import com.inwave.api.dto.map.toDomain
import com.inwave.api.dto.track.AdditionalTrackData
import com.inwave.api.dto.track.FetchAdditionalTrackDataRequest
import com.inwave.domain.entity.Track
import com.inwave.domain.service.TrackAdditionalDataService
import com.inwave.domain.service.VideoShotProviderService
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.UUID
import kotlin.coroutines.resumeWithException

class TrackAdditionalDataRabbitMqService(
    private val channel: Channel,
    private val videoShotProviderService: VideoShotProviderService
) : TrackAdditionalDataService {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun fetchTrackAdditionalData(
        track: Track
    ): Result<Track.AdditionalData> = withContext(Dispatchers.IO) {
        runCatching {
            val requestId = UUID.randomUUID().toString()

            val trackDataRequest = FetchAdditionalTrackDataRequest(
                id = track.id,
                name = track.name,
                albumName = track.release?.name ?: "unknown",
                artists = track.artists.joinToString(" ") { it.artist.name } + track.release?.artists?.joinToString(" ") { it.name }
            )

            channel.basicPublish(
                "track-additional-data-exchange",
                "track.data.request",
                AMQP.BasicProperties.Builder()
                    .correlationId(requestId)
                    .replyTo("track-additional-data-response")
                    .build(),
                Json.encodeToString(trackDataRequest).toByteArray()
            )

            return@runCatching json.decodeFromString<AdditionalTrackData>(
                getResponse(requestId),
            ).toDomain(
                trackId = track.id,
                videoShotProviderService = videoShotProviderService
            )
        }
    }

    private suspend fun getResponse(correlationId: String): String = suspendCancellableCoroutine { continuation ->
        val consumerTag = channel.basicConsume(
            "track-additional-data-response",
            false,
            { tag, delivery ->
                if (delivery.properties.correlationId == correlationId) {
                    channel.basicAck(delivery.envelope.deliveryTag, false)
                    continuation.resume(
                        String(delivery.body)
                    ) { cause, _, _ -> continuation.resumeWithException(cause) }
                } else {
                    channel.basicNack(delivery.envelope.deliveryTag, false, true)
                }
            },
            {
                continuation.resumeWithException(
                    RuntimeException("Consumer #$it cancelled")
                )
            }
        )

        continuation.invokeOnCancellation {
            channel.basicCancel(consumerTag)
        }
    }
}