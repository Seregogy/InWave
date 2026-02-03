package com.inwave.player

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
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
import com.inwave.MainActivity
import com.inwave.R
import com.inwave.player.state.PlayerState
import com.inwave.player.state.PlayerStateSource
import com.inwave.tool.mediaItems
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject


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

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        val sessionActivityPending = createSessionActivityPendingIntent()

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true)
            .setLoadControl(playerLoadControl)
            .build()

//        val mediaSession = MediaSessionCompat(this, "PlayerService")
//
//// Create a MediaStyle object and supply your media session token to it.
//        val mediaStyle = Notification.MediaStyle().setMediaSession(mediaSession.sessionToken)
//
//// Create a Notification which is styled by your MediaStyle object.
//// This connects your media session to the media controls.
//// Don't forget to include a small icon.
//        val notification = Notification.Builder(this@PlayerService, CHANNEL_ID)
//            .setStyle(mediaStyle)
//            .setSmallIcon(R.drawable.ic_app_logo)
//            .build()

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivityPending)
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

    private fun createSessionActivityPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            action = Intent.ACTION_MAIN
            putExtra("from_notification", true)
        }

        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
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