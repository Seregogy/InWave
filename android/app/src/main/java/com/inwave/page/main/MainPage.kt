package com.inwave.page.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.inwave.R
import com.inwave.control.ArtistWithTracks
import com.inwave.control.Section
import com.inwave.control.mini.ReleaseMini
import com.inwave.control.scaffold.ErrorDrawer
import com.inwave.control.scaffold.color.ColoredScaffoldState
import com.inwave.control.scaffold.fling.FlingScrollScaffold
import com.inwave.control.scaffold.fling.FlingScrollScaffoldState
import com.inwave.control.scaffold.fling.rememberFlingScaffoldState
import com.inwave.viewmodel.MainPageViewModel
import com.inwave.viewmodel.MainPageViewModelState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainPage(
    padding: PaddingValues,
    viewModel: MainPageViewModel,
    coloredScaffoldState: ColoredScaffoldState,
    hazeState: HazeState,

    isPlay: State<Boolean>,
    onTrackClick: (trackId: String) -> Unit,
    onReleaseClick: (releaseId: String) -> Unit,
    onArtistClick: (artistId: String) -> Unit,
    onLocalTrackPageClick: () -> Unit,
    onInwaveClick: () -> Unit,
    onUserClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val wave = viewModel.wave.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    when(val currentState = state) {
        MainPageViewModelState.Idle -> { }
        MainPageViewModelState.Loading -> { }
        is MainPageViewModelState.Success -> {
            DrawMainPage(
                padding = padding,
                state = currentState,
                coloredScaffoldState = coloredScaffoldState,
                hazeState = hazeState,
                isPlay = isPlay,
                wave = wave,
                onTrackClick = onTrackClick,
                onReleaseClick = onReleaseClick,
                onArtistClick = onArtistClick,
                onLocalTrackPageClick = onLocalTrackPageClick,
                onInwaveClick = onInwaveClick,
                onUserClick = onUserClick,
                onStartListener = {
                    viewModel.startListener()
                },
                onPauseListener = {
                    viewModel.pauseListener()
                }
            )
        }
        is MainPageViewModelState.Error -> {
            ErrorDrawer(Modifier.fillMaxSize(), currentState.exception) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TracksOnDeviceButton {
                        onLocalTrackPageClick()
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.loadMainPage()
                            }
                        }
                    ) {
                        Text("Обновить страницу")
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawMainPage(
    padding: PaddingValues,
    state: MainPageViewModelState.Success,
    coloredScaffoldState: ColoredScaffoldState,
    hazeState: HazeState,

    isPlay: State<Boolean>,
    wave: State<FloatArray>,
    onTrackClick: (trackId: String) -> Unit,
    onReleaseClick: (releaseId: String) -> Unit,
    onArtistClick: (artistId: String) -> Unit,
    onLocalTrackPageClick: () -> Unit,
    onInwaveClick: () -> Unit,
    onUserClick: () -> Unit,

    onStartListener: () -> Unit,
    onPauseListener: () -> Unit
) {
    val screenWidth = with(LocalDensity.current) {
        LocalWindowInfo.current.containerSize.width.toDp()
    }

    FlingScrollScaffold(
        modifier = Modifier
            .hazeSource(hazeState)
            .fillMaxSize(),
        state = rememberFlingScaffoldState {
            calcScrollState(50.dp)
        },
        backgroundContent = {
            if (currentItemIndex.value == 0) {
                onStartListener()

                HeadingSection(
                    coloredScaffoldState = coloredScaffoldState,
                    isPlay = isPlay,
                    wave = wave,
                    onClick = onInwaveClick,
                    onUserClick = onUserClick
                )
            } else {
                onPauseListener()
            }
        },
        headingContent = {
            Box(Modifier.height(screenHeight * .8f))
        }
    ) {
        Column {
            Section(
                label = stringResource(R.string.top_artists),
                items = state.topArtistsAndTracks,
            ) {
                ArtistWithTracks(
                    modifier = Modifier
                        .width(screenWidth * .8f),
                    artist = it.first,
                    tracks = it.second,
                    onTrackClick = onTrackClick
                )
            }

            Spacer(Modifier.height(20.dp))

            Section(
                label = stringResource(R.string.top_releases),
                items = state.topReleases,
            ) {
                ReleaseMini(
                    onReleaseClicked = onReleaseClick,
                    release = it
                )
            }

            TracksOnDeviceButton(onLocalTrackPageClick)

            Spacer(Modifier.height(120.dp))
        }
    }
}

@Composable
private fun TracksOnDeviceButton(onLocalTrackPageClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(.1f))
            .clickable {
                onLocalTrackPageClick()
            }
            .padding(25.dp)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterStart),
            text = stringResource(R.string.tracks_on_device),
            fontSize = 20.sp,
            fontWeight = FontWeight.W600,
            color = Color.White
        )

        Icon(
            modifier = Modifier
                .align(Alignment.CenterEnd),
            imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
            contentDescription = "",
            tint = Color.White
        )
    }
}

@Composable
private fun FlingScrollScaffoldState.HeadingSection(
    coloredScaffoldState: ColoredScaffoldState,
    isPlay: State<Boolean>,
    wave: State<FloatArray>,
    onClick: () -> Unit,
    onUserClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(screenHeight * .8f)
            .alpha(alpha.floatValue)
            .fillMaxWidth()
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()

                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, Color.Transparent)
                    ),
                    blendMode = BlendMode.DstIn
                )
            }
    ) {
        with(coloredScaffoldState) {
            Box {
                WaterLevel(
                    stringResource(R.string.app_name),
                    isPlay,
                    wave,
                    onClick
                )

                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 25.dp, end = 5.dp),
                    onClick = onUserClick
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        modifier = Modifier
                            .size(28.dp),
                        contentDescription = "",
                        tint = primaryOrBackgroundColorAnimated.value
                    )
                }
            }
        }
    }
}

private fun FlingScrollScaffoldState.calcScrollState(topPadding: Dp) {
    isHeaderVisible.value = lazyListState.firstVisibleItemIndex == 0
    totalHeight.value = screenHeight * .8f

    if (isHeaderVisible.value) {
        currentOffset.value = with(density) { lazyListState.firstVisibleItemScrollOffset.toDp() }

        alpha.floatValue = (totalHeight.value - topPadding - currentOffset.value) / totalHeight.value
        colorAlpha.floatValue = (totalHeight.value - topPadding - currentOffset.value) / 45.dp
    }
}