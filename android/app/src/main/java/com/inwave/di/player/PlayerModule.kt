package com.inwave.di.player

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import com.inwave.di.RemoteLegacyRepo
import com.inwave.domain.usecase.track.query.GetTrackUseCase
import com.inwave.domain.usecase.track.query.GetTrackWithLyricsUseCase
import com.inwave.domain.usecase.track.query.GetTracksUseCase
import com.inwave.player.state.PlayerStateSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

object DefaultPlayerConfig {
    var isAutoplay: Boolean = false

    var backBufferMs = 120_000

    var minBufferMs = 5_000
    var maxBufferMs = 300_000
    var bufferForPlaybackMs = 5_000
    var bufferForPlaybackAfterRebuffedMs = 5_000

    var targetBufferBytesSize = 24 * 1024 * 1024
}

@Module
@InstallIn(SingletonComponent::class)
class PlayerModule {
    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideLoadControl(): DefaultLoadControl {
        return DefaultLoadControl.Builder()
            .setBackBuffer(DefaultPlayerConfig.backBufferMs, true)
            .setBufferDurationsMs(
                DefaultPlayerConfig.minBufferMs,
                DefaultPlayerConfig.maxBufferMs,
                DefaultPlayerConfig.bufferForPlaybackMs,
                DefaultPlayerConfig.bufferForPlaybackAfterRebuffedMs
            )
            .setTargetBufferBytes(DefaultPlayerConfig.targetBufferBytesSize)
            .build()
    }

    @Provides
    @Singleton
    fun provideAudioPlayer(
        @RemoteLegacyRepo getTrack: GetTrackUseCase,
        @RemoteLegacyRepo getTracks: GetTracksUseCase,
        @RemoteLegacyRepo getTrackWithLyrics: GetTrackWithLyricsUseCase
    ): PlayerStateSource {
        return PlayerStateSource(getTrack, getTracks, getTrackWithLyrics)
    }
}