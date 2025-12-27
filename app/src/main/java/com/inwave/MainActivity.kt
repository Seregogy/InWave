package com.inwave

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.inwave.domain.repository.TrackRepository
import com.inwave.domain.usecase.track.GetTrackUseCase
import com.inwave.domain.usecase.track.GetTracksUseCase
import com.inwave.page.TracksPlaylist
import com.inwave.player.MediaControllerInitializer
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
    @Inject lateinit var playerStateSource: PlayerStateSource
    @Inject lateinit var getTrackUseCase: GetTracksUseCase

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
                        NavRoutes(innerPadding, playerStateSource, getTrackUseCase)
                    }
                }
            }
        }
    }
}

@Composable
fun NavRoutes(
    innerPadding: PaddingValues,
    playerStateSource: PlayerStateSource,
    getTracksUseCase: GetTracksUseCase
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "LocalTrackList"
    ) {
        composable(
            route = "LocalTrackList"
        ) {
            println(playerStateSource)
            TracksPlaylist(innerPadding)
        }

        composable(
            route = "TrackPage?id={trackId}",
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "inwave://app/albums/{trackId}"
                }
            ),
            arguments = listOf(navArgument("trackId") { type = NavType.StringType })
        ) {
            val trackId = it.arguments?.getString("trackId") ?: ""
            LaunchedEffect(Unit) {
                getTracksUseCase(listOf(trackId)).onSuccess {
                    playerStateSource.setPlaylist(it)
                    playerStateSource.play()

                    navController.popBackStack()
                }
            }

            //TracksPlaylist(innerPadding)
        }
    }
}