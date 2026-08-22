package com.inwave.player.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inwave.R
import com.inwave.control.ArtistsFeatured
import com.inwave.control.menu.ContextMenu
import com.inwave.control.mini.TrackMiniWithImage
import com.inwave.control.scaffold.color.ColoredScaffold
import com.inwave.control.scaffold.color.ColoredScaffoldState
import com.inwave.layout.TagsRow
import com.inwave.player.state.PlayerState
import com.inwave.viewmodel.AudioPlayerViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import java.util.UUID

const val animationsSpeed = 1200

@Composable
fun FullAudioPlayer(
    viewModel: AudioPlayerViewModel,
    modifier: Modifier,
    topPadding: Dp,
    coloredScaffoldState: ColoredScaffoldState,
    onCollapseRequest: () -> Unit = { },
    onAlbumClicked: (albumId: String) -> Unit,
    onArtistClicked: (artistId: String) -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current

    val screenWidthDp = windowInfo.containerDpSize.width
    val screenHeightDp = windowInfo.containerDpSize.height

    val track by viewModel.track.collectAsStateWithLifecycle()
    val playlist by viewModel.playlist.collectAsStateWithLifecycle()

    val trackDuration by viewModel.trackDuration.collectAsStateWithLifecycle(1L)
    val state by viewModel.playerState.collectAsStateWithLifecycle()
    val currentPosition = viewModel.currentPosition.collectAsStateWithLifecycle()
    val isLastTrack = viewModel.isLastTrack.collectAsStateWithLifecycle()
    val isPlay = viewModel.isPlaying.collectAsStateWithLifecycle()

    val isLoading = remember {
        derivedStateOf {
            state == PlayerState.Loading()
        }
    }

    val isDescriptionExpanded = remember { mutableStateOf(false) }

    val isSliding = remember { mutableStateOf(false) }
    val isLyricsOpen = remember { mutableStateOf(false) }

    val showQueue = remember { mutableStateOf(false) }

    val snapPosition = object : SnapPosition {
        override fun position(
            layoutSize: Int,
            itemSize: Int,
            beforeContentPadding: Int,
            afterContentPadding: Int,
            itemIndex: Int,
            itemCount: Int,
        ): Int {
            return beforeContentPadding / 2
        }
    }

    val lazyListState = rememberLazyListState()
    val snapLayoutInfoProvider = SnapLayoutInfoProvider(lazyListState, snapPosition)

    var topControlsHeight by remember { mutableStateOf(0.dp) }

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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColorAnimated.value)
                .background(additionalVerticalGradientBrush.value)
        )

        Box {
            LazyColumn(
                modifier = Modifier
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                    .drawWithContent {
                        drawContent()

                        drawRect(
                            brush = Brush.verticalGradient(
                                0f to Color.Transparent,
                                ((topControlsHeight.roundToPx() + topPadding.roundToPx()) / screenHeightDp.roundToPx()
                                    .toFloat()) to Color.Black
                            ),
                            blendMode = BlendMode.DstIn
                        )
                    },
                state = lazyListState,
                flingBehavior = rememberSnapFlingBehavior(snapLayoutInfoProvider)
            ) {
                item {
                    Box {
                        Column(
                            modifier = Modifier
                                .height(screenHeightDp)
                                .then(modifier)
                        ) {
                            Spacer(Modifier.height(topControlsHeight))

                            AnimatedVisibility(
                                showQueue.value.not(),
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Column {
                                    MainContent(
                                        viewModel = viewModel,
                                        isLyricsOpen = isLyricsOpen,
                                        screenWidth = screenWidthDp
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
                                                onReleaseClick = onAlbumClicked,
                                                onArtistClick = onArtistClicked
                                            )

                                            Spacer(Modifier.height(10.dp))

                                            PlayerSlider(
                                                currentTrackDuration = trackDuration,
                                                viewModel = viewModel,
                                                isSliding = isSliding,
                                                onLikeClick = {
                                                    viewModel.like()
                                                }
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
                                            onNext = { viewModel.seekToNext() },
                                            onPrev = { viewModel.seekToPrev() },
                                            onPlayPause = { viewModel.playPause() }
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

                        AnimatedVisibility(
                            showQueue.value,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Box(Modifier.fillMaxSize()) {
                                LazyColumn {
                                    item { Spacer(Modifier.height(10.dp)) }
                                    items(playlist, { UUID.randomUUID() }) { track ->
                                        TrackMiniWithImage(
                                            modifier = Modifier
                                                .padding(vertical = 5.dp)
                                                .padding(horizontal = 30.dp),
                                            track = track,
                                            onPrimaryColor = onBackgroundColorAnimated.value,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .height(screenHeightDp)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        lastPaletteColorAnimated.value
                                    )
                                )
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .animateContentSize(),
                            verticalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            Spacer(Modifier.height(topControlsHeight + 25.dp))

                            if (track?.additionalData?.descriptionPreviewPlainText?.isNotBlank() ?: false) {
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 25.dp)
                                        .clip(MaterialTheme.shapes.small)
                                        .clickable {
                                            isDescriptionExpanded.value = !isDescriptionExpanded.value
                                        }
                                        .background(onBackgroundColorAnimated.value.copy(.07f))
                                        .padding(15.dp)
                                ) {
                                    Text(
                                        text = track?.additionalData?.descriptionPreviewPlainText!! + "...",
                                        style = TextStyle(
                                            color = onBackgroundColorAnimated.value,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.W500
                                        )
                                    )
                                }
                            }

                            track?.additionalData?.tags?.let { tags ->
                                Box(Modifier.padding(horizontal = 25.dp)) {
                                    TagsRow(
                                        horizontalSpace = 8.dp,
                                        verticalSpace = 8.dp
                                    ) {
                                        tags.take(3).forEach { tag ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .background(onBackgroundColorAnimated.value.copy(.07f))
                                                    .clickable {
                                                        //TODO: Поиск по тегу при нажатии
                                                    }
                                                    .padding(horizontal = 10.dp)
                                                    .padding(vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "#${tag}",
                                                    fontWeight = FontWeight.W500,
                                                    fontSize = 12.sp,
                                                    color = onBackgroundColorAnimated.value
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            track?.artists?.let { artists ->
                                Column {
                                    Text(
                                        text = "Исполнители",
                                        modifier = Modifier
                                            .padding(start = 25.dp),
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.W800,
                                        color = onBackgroundColor.value
                                    )

                                    Spacer(Modifier.height(20.dp))

                                    ArtistsFeatured(
                                        modifier = Modifier,
                                        cardModifier = Modifier
                                            .width(screenWidthDp - 50.dp)
                                            .height(450.dp),
                                        horizontalPaddings = 25.dp,
                                        artists = artists.map { it.artist }
                                    ) {
                                        onArtistClicked(it.id)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Box(Modifier.align(Alignment.TopCenter)) {
                TopBar(
                    modifier = modifier
                        .onSizeChanged {
                            topControlsHeight = with(density) {
                                it.height.toDp()
                            }
                        },
                    track = track,
                    onCollapseRequest = onCollapseRequest,
                    onShowQueueRequest = { showQueue.value = !showQueue.value }
                )
            }
        }
    }

    ContextMenu(
        expanded = isDescriptionExpanded,
        label = track?.name,
        description = stringResource(R.string.description),
        isDraggable = true
    ) {
        MarkdownText(
            modifier = Modifier
                .padding(30.dp),
            markdown = track?.additionalData?.descriptionMarkdown ?: "empty",
            syntaxHighlightColor = Color.White.copy(.07f),
            style = TextStyle(
                color = Color.White.copy(.7f),
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )
        )
    }
}