package com.inwave.player

import android.os.Bundle
import android.util.Log
import androidx.media3.common.C
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
import com.inwave.player.state.PlayerStateHandler
import com.inwave.player.state.PlayerStateSource
import com.inwave.tool.mediaItems
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@AndroidEntryPoint
class InWaveMediaSessionService: MediaSessionService() {
    @Inject lateinit var playerLoadControl: DefaultLoadControl

    lateinit var player: ExoPlayer
    lateinit var mediaSession: MediaSession

    val favoriteCommand = SessionCommand(ACTIVITY_SERVICE, Bundle.EMPTY)
    val unfavoriteCommand = SessionCommand(ACCOUNT_SERVICE, Bundle.EMPTY)

    override fun onCreate() {
        super.onCreate()

        val favoriteButton =
            CommandButton.Builder(CommandButton.ICON_HEART_UNFILLED)
                .setDisplayName("Save to favorites")
                .setSessionCommand(favoriteCommand)
                .build()

        val unfavoriteButton =
            CommandButton.Builder(CommandButton.ICON_MINUS_CIRCLE_UNFILLED)
                .setDisplayName("Save to unfavorites")
                .setSessionCommand(unfavoriteCommand)
                .build()
        player = ExoPlayer.Builder(this)
            .setLoadControl(playerLoadControl)
            .build()

        mediaSession = MediaSession.Builder(this, player)
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
        Log.d("InWaveMediaSessionService", "${mediaSession}, ${player}")
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaSession.run {
            player.release()
            release()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }
}