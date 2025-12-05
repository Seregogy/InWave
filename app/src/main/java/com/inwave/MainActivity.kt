package com.inwave

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.compose.rememberNavController
import com.google.common.util.concurrent.MoreExecutors
import com.inwave.page.TracksPlaylist
import com.inwave.player.InWaveMediaSessionService
import com.inwave.player.MediaControllerInitializer
import com.inwave.player.state.PlayerStateHandler
import com.inwave.player.state.PlayerStateSource
import com.inwave.player.ui.AudioPlayerScaffold
import com.inwave.ui.theme.InWaveTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dev.chrisbanes.haze.rememberHazeState
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {
    @Inject lateinit var playerControllerInitializer: MediaControllerInitializer

    override fun onCreate() {
        super.onCreate()
        playerControllerInitializer.initialize()
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            InWaveTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AudioPlayerScaffold(
                        innerPadding = innerPadding,
                        hazeState = rememberHazeState(),
                        navController = rememberNavController()
                    ) { sheetPeekHeight, innerPadding ->
                        TracksPlaylist(innerPadding)
                    }
                }
            }
        }
    }
}