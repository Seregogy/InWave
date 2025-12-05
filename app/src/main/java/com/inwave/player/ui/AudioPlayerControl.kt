package com.inwave.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inwave.control.scaffold.color.ColoredScaffold
import com.inwave.control.scaffold.color.ColoredScaffoldState
import com.inwave.player.state.PlayerCommand
import com.inwave.player.state.PlayerState
import com.inwave.viewmodel.AudioPlayerViewModel

const val animationsSpeed = 1200

@Composable
fun FullAudioPlayer(
    viewModel: AudioPlayerViewModel,
    modifier: Modifier,
    coloredScaffoldState: ColoredScaffoldState,
    onCollapseRequest: () -> Unit = { },
    onAlbumClicked: (albumId: String) -> Unit,
    onArtistClicked: (artistId: String) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val track by viewModel.playerStateSource.currentTrack.collectAsStateWithLifecycle()
    val lyrics by remember {
        derivedStateOf {
            track?.lyrics
        }
    }

    val trackDuration by viewModel.playerStateSource.currentTrackDuration.collectAsStateWithLifecycle()
    val state by viewModel.playerStateSource.currentState.collectAsStateWithLifecycle()
    val currentPosition = viewModel.playerStateSource.currentPosition.collectAsStateWithLifecycle()
    val isLastTrack = viewModel.playerStateSource.isLastTrack.collectAsStateWithLifecycle()
    val isPlay = viewModel.playerStateSource.isPlaying.collectAsStateWithLifecycle()

    val isLoading = remember {
        derivedStateOf {
            state == PlayerState.Loading()
        }
    }

    val isSliding = remember { mutableStateOf(false) }
    val isLyricsOpen = remember { mutableStateOf(false) }

    LaunchedEffect(track) {
        isLyricsOpen.value = isLyricsOpen.value && lyrics != null
    }

    ColoredScaffold(coloredScaffoldState) {
        val secondaryColorWithLoadingState by remember {
            derivedStateOf {
                if (isLoading.value) {
                    onBackgroundColorAnimated.value.copy(.5f)
                } else {
                    onBackgroundColorAnimated.value
                }
            }
        }

        Column(
            modifier = Modifier
                .background(backgroundColorAnimated.value)
                .background(additionalVerticalGradientBrush.value)
                .then(modifier)
        ) {
            TopBar(
                track = track,
                onCollapseRequest = onCollapseRequest
            )

            MainContent(
                viewModel = viewModel,
                isLyricsOpen = isLyricsOpen,
                screenWidth = screenWidth
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (-50).dp)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    TrackInfo(
                        track = track,
                        isTrackLoading = isLoading,
                        onAlbumClicked = onAlbumClicked,
                        onArtistClicked = onArtistClicked
                    )

                    Spacer(Modifier.height(10.dp))

                    PlayerSlider(
                        currentPosition = currentPosition,
                        currentTrackDuration = trackDuration,
                        viewModel = viewModel,
                        isSliding = isSliding
                    )

                    TimingText(
                        secondaryColorWithLoadingState = secondaryColorWithLoadingState,
                        currentPosition = currentPosition,
                        currentTrackDuration =  trackDuration,
                        isSliding = isSliding
                    )
                }

                PlayerNavigationButtons(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    secondaryColorWithLoadingState = secondaryColorWithLoadingState,
                    isPlay = isPlay,
                    isLastTrack = isLastTrack,
                    onNext = { viewModel.playerStateSource.seekToNext() },
                    onPrev = { viewModel.playerStateSource.seekToPrev() },
                    onPlayPause = { viewModel.playerStateSource.playPause() }
                )

                BottomControls(
                    modifier = Modifier
                        .fillMaxWidth(),
                    viewModel = viewModel,
                    isLyricsOpen = isLyricsOpen
                )
            }
        }
    }
}