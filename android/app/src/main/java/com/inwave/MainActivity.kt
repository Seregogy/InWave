package com.inwave

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.inwave.control.scaffold.color.ColoredScaffold
import com.inwave.control.scaffold.color.rememberColoredScaffoldState
import com.inwave.di.RemoteRepo
import com.inwave.domain.repository.command.UserCommandRepository
import com.inwave.domain.usecase.release.query.GetReleaseTracksUseCase
import com.inwave.domain.usecase.track.query.GetTracksUseCase
import com.inwave.page.AuthScreen
import com.inwave.page.TracksPlaylist
import com.inwave.page.artist.ArtistPage
import com.inwave.page.artist.ArtistPageRefreshable
import com.inwave.page.main.MainPage
import com.inwave.page.release.ReleasePage
import com.inwave.page.release.ReleasePageRefreshable
import com.inwave.page.user.UserProfilePage
import com.inwave.page.user.UserProfilePageRefreshable
import com.inwave.player.MediaControllerInitializer
import com.inwave.player.state.PlayerStateSource
import com.inwave.player.ui.AudioPlayerScaffold
import com.inwave.tool.TokenManager
import com.inwave.ui.theme.InWaveTheme
import com.inwave.viewmodel.AudioPlayerViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dev.chrisbanes.haze.HazePositionStrategy
import dev.chrisbanes.haze.HazeState
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
    @Inject lateinit var userRepository: UserCommandRepository
    @Inject lateinit var tokenManager: TokenManager
    @RemoteRepo  @Inject lateinit var getTrackUseCase: GetTracksUseCase
    @RemoteRepo @Inject lateinit var getReleaseTracksUseCase: GetReleaseTracksUseCase

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        window.isNavigationBarContrastEnforced = false

        setContent {
            InWaveTheme {
                val hazeState = rememberHazeState(
                    positionStrategy = HazePositionStrategy.Screen
                )
                val navController = rememberNavController()
                val audioPlayerViewModel = hiltViewModel<AudioPlayerViewModel>()

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                ) { innerPadding ->
                    AudioPlayerScaffold(
                        viewModel = audioPlayerViewModel,
                        innerPadding = innerPadding,
                        hazeState = hazeState,
                        navController = navController
                    ) { sheetPeekHeight, padding, miniPlayerHeight ->
                        NavRoutes(
                            innerPadding = innerPadding,
                            hazeState = hazeState,
                            miniPlayerHeight = miniPlayerHeight,
                            navController = navController,
                            userRepository = userRepository,
                            tokenManager = tokenManager,
                            playerStateSource = playerStateSource,
                            getTracksUseCase = getTrackUseCase,
                            getReleaseTracksUseCase = getReleaseTracksUseCase,
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
    hazeState: HazeState,
    miniPlayerHeight: State<Dp>,
    navController: NavHostController,
    userRepository: UserCommandRepository,
    tokenManager: TokenManager,
    playerStateSource: PlayerStateSource,
    getTracksUseCase: GetTracksUseCase,
    getReleaseTracksUseCase: GetReleaseTracksUseCase,
    audioPlayerViewModel: AudioPlayerViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val startDestination = if (tokenManager.hasToken()) "/" else "/auth"

    NavHost(
        navController = navController,
        startDestination = startDestination
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
                    hazeState = hazeState,
                    isPlay = playerStateSource.isPlaying.collectAsStateWithLifecycle(),
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
                    onLocalTrackPageClick = { navController.navigate("/tracks/local") },
                    onInwaveClick = {
                        coroutineScope.launch {
                            playerStateSource.playPause()
                        }
                    },
                    onUserClick = {
                        navController.navigate("/profile")
                    }
                )
            }
        }

        composable("/auth") {
            val errorText = remember { mutableStateOf("") }

            AuthScreen(
                errorText = errorText,
                onLogin = { userName, password ->
                    errorText.value = ""

                    coroutineScope.launch {
                        userRepository.login(userName, password).onSuccess {
                            tokenManager.saveToken(it.token)

                            navController.navigate("/") {
                                popUpTo("/auth") { inclusive = true }
                            }
                        }.onFailure { errorText.value = it.message.toString() }
                    }
                },
                onRegister = { userName, password ->
                    errorText.value = ""

                    coroutineScope.launch {
                        userRepository.register(userName, password).onSuccess {
                            tokenManager.saveToken(it.token)
                            navController.navigate("/") {
                                popUpTo("/auth") { inclusive = true }
                            }
                        }.onFailure { errorText.value = it.message.toString() }
                    }
                }
            )
        }

        composable("/profile") {
            UserProfilePageRefreshable(
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
                onReleaseClick = { navController.navigate("/releases/$it") },
                onLoginClick = {
                    navController.navigate("/auth") {
                        popUpTo("/profile") { inclusive = true }
                    }
                }
            )
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
            TracksPlaylist(innerPadding, miniPlayerHeight)
        }

        composable(
            route = "/releases/{releaseId}",
            arguments = listOf(navArgument("releaseId") { type = NavType.StringType })
        ) {
            ReleasePageRefreshable(
                viewModel = hiltViewModel(),
                hazeState = hazeState,
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
                        getReleaseTracksUseCase(it).onSuccess {
                            playerStateSource.setPlaylist(it)
                            playerStateSource.play()
                        }
                    }
                }
            )
        }

        composable(
            route = "/artists/{artistId}",
            arguments = listOf(navArgument("artistId") { type = NavType.StringType })
        ) {
            ArtistPageRefreshable(
                viewModel = hiltViewModel(),
                hazeState = hazeState,
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