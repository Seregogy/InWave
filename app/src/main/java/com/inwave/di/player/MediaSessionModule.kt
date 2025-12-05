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
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.inwave.player.state.PlayerStateHandler
import com.inwave.player.state.PlayerStateSource
import com.inwave.tool.mediaItems
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
@OptIn(UnstableApi::class)
object MediaSessionModule {
    @Provides
    @ServiceScoped
    fun providePlayerStateHandler(
        playerStateSource: PlayerStateSource,
        mediaController: MediaController
    ): PlayerStateHandler {
        return PlayerStateHandler(
            playerStateSource,
            mediaController
        )
    }
}