package com.inwave

import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.inwave.control.scaffold.color.ColoredScaffold
import com.inwave.control.scaffold.color.rememberColoredScaffoldState
import com.inwave.di.RemoteLegacyRepo
import com.inwave.domain.usecase.track.query.GetTracksUseCase
import com.inwave.page.TracksPlaylist
import com.inwave.page.artist.ArtistPage
import com.inwave.page.main.MainPage
import com.inwave.page.release.ReleasePage
import com.inwave.player.MediaControllerInitializer
import com.inwave.player.state.PlayerStateSource
import com.inwave.player.ui.AudioPlayerScaffold
import com.inwave.ui.theme.InWaveTheme
import com.inwave.viewmodel.AudioPlayerViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch
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
    @RemoteLegacyRepo @Inject lateinit var getTrackUseCase: GetTracksUseCase
    //@RemoteLegacyRepo @Inject lateinit var getReleaseTracksUseCase: GetReleaseTracksUseCase

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            InWaveTheme {
                val navController = rememberNavController()
                val audioPlayerViewModel = hiltViewModel<AudioPlayerViewModel>()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AudioPlayerScaffold(
                        viewModel = audioPlayerViewModel,
                        innerPadding = innerPadding,
                        hazeState = rememberHazeState(),
                        navController = navController
                    ) { sheetPeekHeight, padding ->
                        NavRoutes(
                            innerPadding = innerPadding,
                            navController = navController,
                            playerStateSource = playerStateSource,
                            getTracksUseCase = getTrackUseCase,
                            //getReleaseTracksUseCase = getReleaseTracksUseCase,
                            audioPlayerViewModel = audioPlayerViewModel
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavRoutes(
    innerPadding: PaddingValues,
    navController: NavHostController,
    playerStateSource: PlayerStateSource,
    getTracksUseCase: GetTracksUseCase,
    //getReleaseTracksUseCase: GetReleaseTracksUseCase,
    audioPlayerViewModel: AudioPlayerViewModel
) {
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = "/"
//        startDestination = "/artists/54af8669-44d9-4a1c-bbeb-f5f858274445"
//        startDestination = "/tracks/local",
//        startDestination = "/releases/release_twelve_carat_toothache"
    ) {
        composable("/") {
            ColoredScaffold(
                state = rememberColoredScaffoldState {
                    audioPlayerViewModel.imagePaletteExtractor.palette.collectAsState()
                }
            ) {

                Box(Modifier.fillMaxSize().background(backgroundColorAnimated.value.copy(.25f)))
                MainPage(
                    padding = innerPadding,
                    viewModel = hiltViewModel(),
                    coloredScaffoldState = this,
                    onTrackClick = {
                        coroutineScope.launch {
                            getTracksUseCase(listOf(it)).onSuccess {
                                playerStateSource.setPlaylist(it)
                                playerStateSource.play()
                            }
                        }
                    },
                    onReleaseClick = { navController.navigate("/releases/${it}") },
                    onArtistClick = { navController.navigate("/artists/${it}") },
                    onLocalTrackPageClick = { navController.navigate("/tracks/local") }
                )
            }
        }

        composable(
            route = "/tracks/{trackId}",
            arguments = listOf(navArgument("trackId") { type = NavType.StringType })
        ) {
            val trackId = it.arguments?.getString("trackId") ?: ""

            LaunchedEffect(Unit) {
                getTracksUseCase(listOf(trackId)).onSuccess {
                    playerStateSource.setPlaylist(it)
                    playerStateSource.play()
                }
            }
        }

        composable(
            route = "/tracks/local"
        ) {
            println(playerStateSource)
            TracksPlaylist(innerPadding)
        }

        composable(
            route = "/releases/{releaseId}",
            arguments = listOf(navArgument("releaseId") { type = NavType.StringType })
        ) {
            ReleasePage(
                viewModel = hiltViewModel(),
                innerPadding = innerPadding,
                bottomPadding = innerPadding.calculateBottomPadding(),
                onBackRequest = { navController.popBackStack() },
                onReleaseClick = { navController.navigate("/releases/${it}") },
                onArtistClick = { navController.navigate("/artists/${it}") },
                onTrackClick = {
                    coroutineScope.launch {
                        getTracksUseCase(listOf(it)).onSuccess {
                            playerStateSource.setPlaylist(it)
                            playerStateSource.play()
                        }
                    }
                },
                onReleasePlayClick = {
                    coroutineScope.launch {
                        /*getReleaseTracksUseCase(it).onSuccess {
                            playerStateSource.setPlaylist(it)
                            playerStateSource.play()
                        }*/
                    }
                }
            )
        }

        composable(
            route = "/artists/{artistId}",
            arguments = listOf(navArgument("artistId") { type = NavType.StringType })
        ) {
            ArtistPage(
                viewModel = hiltViewModel(),
                innerPadding = innerPadding,
                bottomPadding = innerPadding.calculateBottomPadding(),
                onBackRequest = { navController.popBackStack() },
                onTrackClick = {
                    coroutineScope.launch {
                        getTracksUseCase(listOf(it)).onSuccess {
                            playerStateSource.setPlaylist(it)
                            playerStateSource.play()
                        }
                    }
                },
                onReleaseClick = { navController.navigate("/releases/${it}") }
            )
        }
    }
}