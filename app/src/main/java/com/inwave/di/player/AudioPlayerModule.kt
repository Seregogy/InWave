package com.inwave.di.player

import android.content.Context
import android.content.ServiceConnection
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import com.inwave.domain.repository.TrackRepository
import com.inwave.player.AudioPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(ServiceComponent::class)
class AudioPlayerModule {
    @Provides
    @ServiceScoped
    fun provideMediaController(
        @ApplicationContext context: Context,
        mediaSession: MediaSession
    ): MediaController {
        return MediaController.Builder(context, mediaSession.token)
            .buildAsync()
            .get()
    }

    @Provides
    @ServiceScoped
    fun provideAudioPlayer(
        mediaController: MediaController,
        trackRepository: TrackRepository
    ): AudioPlayer {
        return AudioPlayer(mediaController, trackRepository)
    }
}