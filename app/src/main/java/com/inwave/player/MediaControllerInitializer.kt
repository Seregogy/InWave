package com.inwave.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.inwave.player.state.PlayerStateHandler
import com.inwave.player.state.PlayerStateSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaControllerInitializer @Inject constructor(
    @ApplicationContext val context: Context,
    val playerStateSource: PlayerStateSource
) {
    fun initialize() {
        val sessionToken =
            SessionToken(context, ComponentName(context, InWaveMediaSessionService::class.java))

        val controllerFuture = MediaController.Builder(context, sessionToken)
            .buildAsync()

        controllerFuture.addListener({
            PlayerStateHandler(
                playerStateSource,
                controllerFuture.get()
            )
        }, MoreExecutors.directExecutor())
    }
}