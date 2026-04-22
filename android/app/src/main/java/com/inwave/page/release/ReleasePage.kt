package com.inwave.page.release

import android.graphics.Bitmap
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Downloading
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.inwave.control.CircleButton
import com.inwave.control.scaffold.ErrorDrawer
import com.inwave.control.Section
import com.inwave.control.mini.ReleaseMini
import com.inwave.control.mini.TrackMini
import com.inwave.control.scaffold.color.ColoredScaffold
import com.inwave.control.scaffold.color.ColoredScaffoldState
import com.inwave.control.scaffold.color.rememberColoredScaffoldState
import com.inwave.control.scaffold.fling.FlingScrollScaffold
import com.inwave.control.scaffold.fling.FlingScrollScaffoldState
import com.inwave.control.scaffold.fling.rememberFlingScaffoldState
import com.inwave.control.scaffold.tool.ToolScaffold
import com.inwave.control.scaffold.tool.rememberToolScaffoldState
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track
import com.inwave.viewmodel.ReleasePageViewModel
import com.inwave.viewmodel.ReleasePageViewModelState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun ReleasePage(
    viewModel: ReleasePageViewModel,
    innerPadding: PaddingValues,
    bottomPadding: Dp,
    onBackRequest: () -> Unit = { },
    onReleaseClick: (releaseId: String) -> Unit = { },
    onArtistClick: (artistId: String) -> Unit = { },
    onTrackClick: (trackId: String) -> Unit = { },
    onReleasePlayClick: (releaseId: String) -> Unit = { }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val infiniteTransition = rememberInfiniteTransition("infinity transition animation")
    val imageBitmap: Bitmap? by viewModel.bitmap.collectAsStateWithLifecycle()
    val coloredScaffoldState = rememberColoredScaffoldState {
        viewModel.palette.collectAsStateWithLifecycle()
    }

    LaunchedEffect(Unit) {
        viewModel.loadRelease()
    }

    when (val currentState = state) {
        ReleasePageViewModelState.Idle -> { }
        ReleasePageViewModelState.Loading -> {
            ReleasePageSkeleton()
        }
        is ReleasePageViewModelState.Success -> {
            DrawReleasePage(
                currentState,
                coloredScaffoldState,
                innerPadding,
                imageBitmap,
                bottomPadding,
                infiniteTransition,
                onBackRequest,
                onArtistClick,
                onTrackClick,
                onReleaseClick,
                onReleasePlayClick
            )
        }
        is ReleasePageViewModelState.Error -> {
            ErrorDrawer(Modifier.fillMaxSize(), currentState.exception)
        }
    }
}

@Composable
private fun DrawReleasePage(
    state: ReleasePageViewModelState.Success,
    coloredScaffoldState: ColoredScaffoldState,
    innerPadding: PaddingValues,
    imageBitmap: Bitmap?,
    bottomPadding: Dp,
    infiniteTransition: InfiniteTransition,
    onBackRequest: () -> Unit,
    onArtistClick: (String) -> Unit,
    onTrackClick: (String) -> Unit,
    onReleaseClick: (String) -> Unit,
    onReleasePlayClick: (releaseId: String) -> Unit
) {
    val toolBarScaffoldState = rememberToolScaffoldState(onBackRequest = onBackRequest)
    val topBarHazeState = rememberHazeState()
    ColoredScaffold(
        state = coloredScaffoldState
    ) {
        ToolScaffold(
            modifier = Modifier
                .padding(innerPadding),
            hazeState = topBarHazeState,
            state = toolBarScaffoldState
        ) { toolBarInnerPadding ->

            FlingScrollScaffold(
                modifier = Modifier
                    .hazeSource(state = topBarHazeState)
                    .background(Color.Black)
                    .fillMaxSize(),
                listBackground = SolidColor(primaryOrBackgroundColor.value.copy(.25f)),
                state = rememberFlingScaffoldState(
                    yFlingOffset = toolBarInnerPadding.calculateTopPadding()
                ) {
                    calcScrollState(toolBarInnerPadding.calculateTopPadding())

                    toolBarScaffoldState.toolBarTitle.value = if (isHeaderSwiped.value.not()) {
                        state.release.name
                    } else {
                        null
                    }
                },
                backgroundContent = {
                    ReleaseHeaderImage(
                        bitmap = imageBitmap,
                        currentOffset = currentOffset,
                        screenHeight = screenHeight,
                        alpha = alpha
                    )
                },
                headingContent = {
                    AlbumHeader(
                        screenHeight = screenHeight,
                        alpha = alpha,
                        release = state.release,
                        onArtistClick = onArtistClick,
                        onReleasePlayClick = onReleasePlayClick
                    )
                }
            ) {
                ReleaseContent(
                    bottomPadding = bottomPadding,
                    release = state.release,
                    tracks = state.tracks,
                    infiniteTransition = infiniteTransition,
                    otherReleases = state.otherReleases,
                    onTrackClick = { onTrackClick(it.id) },
                    onReleaseClick = onReleaseClick,
                    onTrackHold = {
                        /*toolBarScaffoldState.launchContextAction { padding ->
                            MockAdditionalTrackData(padding, primaryOrBackgroundColor.value)
                        }*/
                    }
                )
            }
        }
    }
}

@Composable
private fun ColoredScaffoldState.AlbumHeader(
    screenHeight: Dp,
    alpha: FloatState,
    release: Release,
    onArtistClick: (albumId: String) -> Unit,
    onReleasePlayClick: (releaseId: String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight * .7f)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(alpha.floatValue)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = release.name,
                    fontWeight = FontWeight.W800,
                    fontSize = 28.sp,
                    lineHeight = 28.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                )

                Row(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable { onArtistClick(release.artists.first().id) }
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    AsyncImage(
                        model = release.artists
                            .firstOrNull()
                            ?.imagesUrl
                            ?.firstOrNull(),
                        contentDescription = "mini artist avatar",
                        modifier = Modifier
                            .size(25.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Text(
                        text = release.artists.first().name,
                        fontWeight = FontWeight.W700,
                        color = Color.White
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth(.85f)
                ) {
                    CircleButton(
                        containerColor = onPrimaryOrBackgroundColor.value,
                        onClick = { },
                        underscoreText = "Скачать",
                        underscoreTextColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Downloading,
                            modifier = Modifier
                                .size(28.dp),
                            contentDescription = "",
                            tint = primaryOrBackgroundColorAnimated.value
                        )
                    }

                    CircleButton(
                        containerColor = onPrimaryOrBackgroundColor.value,
                        onClick = { },
                        underscoreText = "Нравится",
                        underscoreTextColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FavoriteBorder,
                            modifier = Modifier
                                .size(28.dp),
                            contentDescription = "",
                            tint = primaryOrBackgroundColorAnimated.value
                        )
                    }

                    /*CircleButton(
                        containerColor = onPrimaryOrBackgroundColor.value,
                        onClick = { },
                        underscoreText = "Трейлер",
                        underscoreTextColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                            modifier = Modifier
                                .size(28.dp),
                            contentDescription = "",
                            tint = primaryOrBackgroundColorAnimated.value
                        )
                    }*/

                    CircleButton(
                        containerColor = onPrimaryOrBackgroundColor.value,
                        onClick = {
                            onReleasePlayClick(release.id)
                        },
                        underscoreText = "Слушать",
                        underscoreTextColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            modifier = Modifier
                                .size(30.dp),
                            contentDescription = "",
                            tint = primaryOrBackgroundColorAnimated.value
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ColoredScaffoldState.ReleaseContent(
    bottomPadding: Dp,
    release: Release,
    tracks: List<Track>,
    infiniteTransition: InfiniteTransition,
    otherReleases: List<Release>,
    onTrackClick: (trackId: Track) -> Unit,
    onTrackHold: (track: Track) -> Unit,
    onReleaseClick: (artistId: String) -> Unit
) {
    Column {
        Spacer(Modifier.height(20.dp))

        for (track in tracks) {
            TrackMini(
                track = track,
                infiniteTransition = infiniteTransition,
                primaryColor = primaryOrBackgroundColorAnimated.value,
                onPrimaryColor = Color.White,
                onClick = onTrackClick,
                onContextAction = onTrackHold
            )
        }

        Spacer(Modifier.height(20.dp))

        Section(
            label = "Ещё от ${release.artists.first().name}",
            items = otherReleases,
        ) {
            ReleaseMini(onReleaseClick, it)
        }

        Spacer(Modifier.height(bottomPadding))
        Spacer(Modifier.height(120.dp))

    }
}

@Composable
private fun ReleaseHeaderImage(
    modifier: Modifier = Modifier,
    bitmap: Bitmap?,
    currentOffset: State<Dp>,
    screenHeight: Dp,
    alpha: FloatState
) {
    Box(
        modifier = modifier
            .alpha(alpha.floatValue)
            .height(screenHeight * .7f)
            .offset {
                return@offset IntOffset(0, (-currentOffset.value / 4).roundToPx())
            }
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()

                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(.85f),
                            Color.Transparent,
                        )
                    ),
                    blendMode = BlendMode.DstIn
                )
            }
    ) {
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

private fun FlingScrollScaffoldState.calcScrollState(
    topPadding: Dp
) {
    isHeaderVisible.value = lazyListState.firstVisibleItemIndex == 0
    totalHeight.value = screenHeight * .7f

    if (isHeaderVisible.value) {
        currentOffset.value = with(density) { lazyListState.firstVisibleItemScrollOffset.toDp() }

        alpha.floatValue = (totalHeight.value - topPadding - currentOffset.value) / totalHeight.value
        colorAlpha.floatValue = (totalHeight.value - topPadding - currentOffset.value) / 45.dp
    }
}