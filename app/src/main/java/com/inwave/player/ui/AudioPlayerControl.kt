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
import com.inwave.player.AudioPlayer
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

    val currentTrack by viewModel.audioPlayer.currentPlayerTrack.collectAsStateWithLifecycle()
    val currentLyrics by viewModel.audioPlayer.currentLyrics.collectAsStateWithLifecycle()
    val currentTrackDuration by viewModel.audioPlayer.currentTrackDuration.collectAsStateWithLifecycle()
    val state by viewModel.audioPlayer.currentState.collectAsStateWithLifecycle()
    val currentPosition = viewModel.audioPlayer.currentPosition

    val isLastTrack = viewModel.audioPlayer.isLastTrack

    val isPlay = remember {
        derivedStateOf {
            state == AudioPlayer.AudioPlayerState.Play
        }
    }

    val isLoading = remember {
        derivedStateOf {
            state == AudioPlayer.AudioPlayerState.Loading
        }
    }

    val isSliding = remember { mutableStateOf(false) }
    val isLyricsOpen = remember { mutableStateOf(false) }

    LaunchedEffect(currentTrack) {
        isLyricsOpen.value = isLyricsOpen.value && currentLyrics != null
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
                track = currentTrack?.data,
                onCollapseRequest = onCollapseRequest
            )

            MainContent(
                viewModel = viewModel,
                isLyricsOpen = isLyricsOpen,
                currentPosition = currentPosition,
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
                        track = currentTrack?.data,
                        isTrackLoading = isLoading,
                        onAlbumClicked = onAlbumClicked,
                        onArtistClicked = onArtistClicked
                    )

                    Spacer(Modifier.height(10.dp))

                    PlayerSlider(
                        currentPosition = currentPosition,
                        currentTrackDuration = currentTrackDuration,
                        viewModel = viewModel,
                        isSliding = isSliding
                    )

                    TimingText(
                        secondaryColorWithLoadingState = secondaryColorWithLoadingState,
                        currentPosition = currentPosition,
                        currentTrackDuration =  currentTrackDuration,
                        isSliding = isSliding
                    )
                }

                PlayerNavigationButtons(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    secondaryColorWithLoadingState = secondaryColorWithLoadingState,
                    isPlay = isPlay,
                    isLastTrack = isLastTrack,
                    onNext = { viewModel.audioPlayer.seekToNext() },
                    onPrev = { viewModel.audioPlayer.seekToPrev() },
                    onPlayPause = { viewModel.audioPlayer.playPause() }
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