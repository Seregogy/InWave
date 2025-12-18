package com.inwave.player

import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.CommandButton.ICON_UNDEFINED
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.inwave.tool.mediaItems
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.inwave.R
import com.inwave.player.state.PlayerState
import com.inwave.player.state.PlayerStateSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@UnstableApi
@AndroidEntryPoint
class InWaveMediaSessionService: MediaSessionService() {
    companion object {
        private const val FAVORITE_BUTTON = "FAVORITE_BUTTON"
        private const val REPEAT_MODE_BUTTON = "REPEAT_MODE_BUTTON"
    }

    private val handlerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @Inject lateinit var playerLoadControl: DefaultLoadControl
    @Inject lateinit var playerStateSource: PlayerStateSource

    lateinit var player: ExoPlayer
    lateinit var mediaSession: MediaSession

    val favoriteCommand = SessionCommand(FAVORITE_BUTTON, Bundle.EMPTY)
    val repeatModeCommand = SessionCommand(REPEAT_MODE_BUTTON, Bundle.EMPTY)

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        var favoriteButton =
            CommandButton.Builder(CommandButton.ICON_HEART_UNFILLED)
                .setDisplayName("Save to favorites")
                .setSessionCommand(favoriteCommand)
                .build()

        var repeatModeButton =
            CommandButton.Builder(ICON_UNDEFINED)
                .setCustomIconResId(iconByRepeatMode(playerStateSource.currentRepeatModeState.value))
                .setDisplayName("Toggle repeat mode")
                .setSessionCommand(repeatModeCommand)
                .build()

        player = ExoPlayer.Builder(this)
            .setLoadControl(playerLoadControl)
            .build()

        mediaSession = MediaSession.Builder(this, player)
            .setCallback(object : MediaSession.Callback {
                @OptIn(UnstableApi::class)
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

                @OptIn(UnstableApi::class)
                override fun onConnect(
                    session: MediaSession,
                    controller: MediaSession.ControllerInfo
                ): MediaSession.ConnectionResult {
                    return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                        .setMediaButtonPreferences(
                            ImmutableList.of(
                                repeatModeButton,
                                favoriteButton
                            ))
                        .setAvailableSessionCommands(
                            MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                                .add(repeatModeCommand)
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
                        FAVORITE_BUTTON -> {
                            Log.d("Media Session", "TOGGLE FAVORITE ACTION")
                        }
                        REPEAT_MODE_BUTTON -> {
                            handlerScope.launch {
                                playerStateSource.nextRepeatMode()
                            }

                            repeatModeButton = buildRepeatModeButtonByState(
                                playerStateSource.currentRepeatModeState.value
                            )

                            Log.d("Media Session", "TOGGLE REPEAT MODE")
                        }
                    }


                    session.setCustomLayout(listOf(
                        repeatModeButton, favoriteButton
                    ))

                    return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }
            })
            .setMediaButtonPreferences(
                ImmutableList.of(
                    favoriteButton, repeatModeButton
                ))
            .build()

        handlerScope.launch {
            playerStateSource.currentRepeatModeState.collect { state ->
                repeatModeButton = buildRepeatModeButtonByState(state)

                mediaSession.setCustomLayout(listOf(
                    repeatModeButton, favoriteButton
                ))
            }
        }

        Log.d("InWaveMediaSessionService", "$mediaSession, $player")
    }

    private fun buildRepeatModeButtonByState(repeatMode: PlayerState.RepeatMode): CommandButton =
        iconByRepeatMode(repeatMode).let { icon ->
            CommandButton.Builder(ICON_UNDEFINED)
                .setCustomIconResId(icon)
                .setDisplayName("Save to unfavorites")
                .setSessionCommand(repeatModeCommand)
                .build()
        }

    private fun iconByRepeatMode(repeatMode: PlayerState.RepeatMode): Int = when (repeatMode) {
        PlayerState.RepeatMode.Playlist -> R.drawable.repeat_icon
        PlayerState.RepeatMode.Forward -> R.drawable.repeat_off
        PlayerState.RepeatMode.Single -> R.drawable.repeat_icon_1
    }

    override fun onDestroy() {
        player.stop()
        player.release()
        mediaSession.release()

        super.onDestroy()
        Log.d("InWaveMediaSessionService", "Media session destroyed")
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }
}