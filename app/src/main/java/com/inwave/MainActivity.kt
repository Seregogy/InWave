package com.inwave

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.inwave.page.TracksPlaylist
import com.inwave.player.InWaveMediaSessionService
import com.inwave.player.ui.AudioPlayerScaffold
import com.inwave.ui.theme.InWaveTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dev.chrisbanes.haze.rememberHazeState

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startService(Intent(this, InWaveMediaSessionService::class.java))
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