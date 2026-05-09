package com.inwave.di.player

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import com.inwave.di.ApplicationSupervisorCoroutineScope
import com.inwave.di.RemoteRepo
import com.inwave.domain.usecase.track.query.GetRandomTrackIdUseCase
import com.inwave.domain.usecase.track.query.GetTrackUseCase
import com.inwave.domain.usecase.track.query.GetTrackWithLyricsUseCase
import com.inwave.domain.usecase.track.query.GetTracksUseCase
import com.inwave.player.AudioVisualizer
import com.inwave.player.state.PlayerStateSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
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
    fun provideVisualizer(): AudioVisualizer {
        return AudioVisualizer()
    }

    @Provides
    @Singleton
    fun provideAudioPlayer(
        @RemoteRepo getTrack: GetTrackUseCase,
        @RemoteRepo getTracks: GetTracksUseCase,
        @RemoteRepo getRandomTrackId: GetRandomTrackIdUseCase,
        @RemoteRepo getTrackWithLyrics: GetTrackWithLyricsUseCase,
        @ApplicationSupervisorCoroutineScope scope: CoroutineScope
    ): PlayerStateSource {
        return PlayerStateSource(
            getTrack,
            getTracks,
            getRandomTrackId,
            getTrackWithLyrics,
            scope
        )
    }
}