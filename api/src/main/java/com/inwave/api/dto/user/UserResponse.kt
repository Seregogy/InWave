package com.inwave.api.dto.user

import com.inwave.domain.entity.User
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object JavaLocalDateTimeSerializer : KSerializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override val descriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), formatter)
    }
}


@Serializable
data class UserRegisterResponse(
    val token: String,
    @Serializable(with = JavaLocalDateTimeSerializer::class)
    val expiredAt: LocalDateTime
)

@Serializable
data class UserLoginResponse(
    val token: String,
    @Serializable(with = JavaLocalDateTimeSerializer::class)
    val expiredAt: LocalDateTime
)

@Serializable
data class FullUserDto(
    val id: String = "",
    val name: String,
    val avatarUrl: String? = null,
    val isAuthenticated: Boolean = false,
    val likedTracks: List<String>,
    val likedReleases: List<String>
)

fun FullUserDto.toDomain(): User = User(
    id = id,
    name = name,
    avatarUrl = avatarUrl ?: "",
    isAuthenticated = isAuthenticated,
    likedTracks = likedTracks,
    likedReleases = likedReleases
)

fun User.fromDomain(): FullUserDto = FullUserDto(
    id = id,
    name = name,
    avatarUrl = avatarUrl,
    isAuthenticated = isAuthenticated,
    likedTracks = likedTracks,
    likedReleases = likedReleases
)
