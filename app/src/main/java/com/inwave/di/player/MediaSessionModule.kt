package com.inwave.di.player

import android.content.Context
import android.content.Context.ACCOUNT_SERVICE
import android.content.Context.ACTIVITY_SERVICE
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.inwave.domain.repository.TrackRepository
import com.inwave.player.AudioPlayer
import com.inwave.player.InWaveMediaSessionService
import com.inwave.tool.mediaItems
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
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
@OptIn(UnstableApi::class)
object MediaSessionModule {
    @Provides
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
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        loadControl: DefaultLoadControl
    ): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .build()
    }

    @Provides
    @Singleton
    fun provideMediaSession(
        @ApplicationContext context: Context,
        player: ExoPlayer
    ): MediaSession {
        val favoriteCommand = SessionCommand(ACTIVITY_SERVICE, Bundle.EMPTY)
        val favoriteButton =
            CommandButton.Builder(CommandButton.ICON_HEART_UNFILLED)
                .setDisplayName("Save to favorites")
                .setSessionCommand(favoriteCommand)
                .build()

        val unfavoriteCommand = SessionCommand(ACCOUNT_SERVICE, Bundle.EMPTY)
        val unfavoriteButton =
            CommandButton.Builder(CommandButton.ICON_MINUS_CIRCLE_UNFILLED)
                .setDisplayName("Save to unfavorites")
                .setSessionCommand(unfavoriteCommand)
                .build()

        return MediaSession.Builder(context, player)
            .setCallback(object : MediaSession.Callback {
                override fun onPlaybackResumption(
                    session: MediaSession,
                    controller: MediaSession.ControllerInfo
                ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> = session.player.let {
                    if (it.mediaItemCount == 0) {
                        Futures.immediateFuture(
                            MediaSession.MediaItemsWithStartPosition(
                                emptyList(),
                                C.INDEX_UNSET,
                                C.TIME_UNSET
                            )
                        )
                    } else {
                        Futures.immediateFuture(
                            MediaSession.MediaItemsWithStartPosition(
                                it.mediaItems,
                                it.currentMediaItemIndex,
                                it.currentPosition
                            )
                        )
                    }
                }

                override fun onConnect(
                    session: MediaSession,
                    controller: MediaSession.ControllerInfo
                ): MediaSession.ConnectionResult {
                    return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                        .setMediaButtonPreferences(
                            ImmutableList.of(
                                unfavoriteButton,
                                favoriteButton
                            ))
                        .setAvailableSessionCommands(
                            MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                                .add(unfavoriteCommand)
                                .add(favoriteCommand)
                                .build()
                        )
                        .build()
                }

                override fun onCustomCommand(
                    session: MediaSession,
                    controller: MediaSession.ControllerInfo,
                    customCommand: SessionCommand,
                    args: Bundle
                ): ListenableFuture<SessionResult> {
                    when(customCommand.customAction) {
                        "SET_FAVORITE" -> Log.d("Media Session", "SET FAVORITE ACTION")
                        "UNSET_FAVORITE" -> Log.d("Media Session", "UNSET FAVORITE ACTION")
                    }

                    return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }
            })
            .setMediaButtonPreferences(
                ImmutableList.of(
                    favoriteButton, unfavoriteButton
                ))
            .build()
    }
}