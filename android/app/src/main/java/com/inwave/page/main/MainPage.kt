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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@Composable
fun MainPage(
    padding: PaddingValues,
    viewModel: MainPageViewModel,
    coloredScaffoldState: ColoredScaffoldState,

    onTrackClick: (trackId: String) -> Unit,
    onReleaseClick: (releaseId: String) -> Unit,
    onArtistClick: (artistId: String) -> Unit,
    onLocalTrackPageClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadRelease()
    }

    when(val currentState = state) {
        MainPageViewModelState.Idle -> { }
        MainPageViewModelState.Loading -> { }
        is MainPageViewModelState.Success -> {
            DrawMainPage(
                padding = padding,
                state = currentState,
                coloredScaffoldState = coloredScaffoldState,
                onTrackClick = onTrackClick,
                onReleaseClick = onReleaseClick,
                onArtistClick = onArtistClick,
                onLocalTrackPageClick = onLocalTrackPageClick
            )
        }
        is MainPageViewModelState.Error -> {
            ErrorDrawer(Modifier.fillMaxSize(), currentState.exception) {
                TracksOnDeviceButton {
                    onLocalTrackPageClick()
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
    onTrackClick: (trackId: String) -> Unit,
    onReleaseClick: (releaseId: String) -> Unit,
    onArtistClick: (artistId: String) -> Unit,
    onLocalTrackPageClick: () -> Unit
) {
    val screenWidth = with(LocalDensity.current) {
        LocalWindowInfo.current.containerSize.width.toDp()
    }

    FlingScrollScaffold(
        modifier = Modifier
            .fillMaxSize(),
        state = rememberFlingScaffoldState {
            calcScrollState(20.dp)
        },
        backgroundContent = {
            val isPressed = remember { mutableStateOf(false) }
            if (currentItemIndex.value == 0) {
                HeadingSection(
                    coloredScaffoldState = coloredScaffoldState,
                    isPressed = isPressed
                )
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
    isPressed: MutableState<Boolean>
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
            WaterLevel(stringResource(R.string.app_name), isPressed)
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