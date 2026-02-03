package com.inwave.di.player

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import com.inwave.player.state.PlayerStateHandler
import com.inwave.player.state.PlayerStateSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
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