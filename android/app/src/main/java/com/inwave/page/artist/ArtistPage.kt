package com.inwave.page.artist

import androidx.compose.animation.core.EaseIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Headset
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.inwave.R
import com.inwave.control.CirclePagerIndicator
import com.inwave.control.Section
import com.inwave.control.mini.ReleaseMini
import com.inwave.control.mini.TrackMiniWithImage
import com.inwave.control.scaffold.ErrorDrawer
import com.inwave.control.scaffold.color.ColoredScaffold
import com.inwave.control.scaffold.color.ColoredScaffoldState
import com.inwave.control.scaffold.color.rememberColoredScaffoldState
import com.inwave.control.scaffold.fling.FlingScrollScaffold
import com.inwave.control.scaffold.fling.FlingScrollScaffoldState
import com.inwave.control.scaffold.fling.rememberFlingScaffoldState
import com.inwave.control.scaffold.tool.ToolScaffold
import com.inwave.control.scaffold.tool.rememberToolScaffoldState
import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track
import com.inwave.viewmodel.ArtistPageViewModel
import com.inwave.viewmodel.ArtistPageViewModelState
import dev.chrisbanes.haze.HazePositionStrategy
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeProgressive
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

const val TOP_PART_WEIGHT = .55f

@Composable
fun ArtistPageRefreshable(
    viewModel: ArtistPageViewModel,
    hazeState: HazeState,
    innerPadding: PaddingValues,
    bottomPadding: Dp,
    onBackRequest: () -> Unit = { },
    onTrackClick: (trackId: String) -> Unit = { },
    onReleaseClick: (albumId: String) -> Unit = { }
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)
    val pullToRefreshState = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()

    val isRefreshing by remember {
        derivedStateOf {
            isLoading && pullToRefreshState.distanceFraction > 0
        }
    }

    PullToRefreshBox(
        modifier = Modifier
            .fillMaxSize(),
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = {
            coroutineScope.launch {
                viewModel.loadArtist()
            }
        }
    ) {
        ArtistPage(
            viewModel = viewModel,
            hazeState = hazeState,
            innerPadding = innerPadding,
            bottomPadding = bottomPadding,
            onBackRequest = onBackRequest,
            onTrackClick = onTrackClick,
            onReleaseClick = onReleaseClick
        )
    }
}

@Composable
fun ArtistPage(
    viewModel: ArtistPageViewModel,
    hazeState: HazeState,
    innerPadding: PaddingValues,
    bottomPadding: Dp,
    onBackRequest: () -> Unit = { },
    onTrackClick: (trackId: String) -> Unit = { },
    onReleaseClick: (albumId: String) -> Unit = { }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val coloredScaffoldState = rememberColoredScaffoldState {
        viewModel.palette.collectAsStateWithLifecycle()
    }

    when(val currentState = state) {
        ArtistPageViewModelState.Idle -> { }
        ArtistPageViewModelState.Loading -> ArtistPageSkeleton()
        is ArtistPageViewModelState.Success -> {
            val pagerState = rememberPagerState(0) { currentState.artist.imagesUrl.size }

            DrawArtistPage(
                currentState,
                coloredScaffoldState,
                hazeState = hazeState,
                pagerState,
                innerPadding,
                bottomPadding,
                onBackRequest,
                onTrackClick,
                onReleaseClick
            )
        }
        is ArtistPageViewModelState.Error ->
            ErrorDrawer(Modifier.fillMaxSize(), currentState.exception)
    }
}

@Composable
private fun DrawArtistPage(
    state: ArtistPageViewModelState.Success,
    coloredScaffoldState: ColoredScaffoldState,
    hazeState: HazeState,
    pagerState: PagerState,
    innerPadding: PaddingValues,
    bottomPadding: Dp,
    onBackRequest: () -> Unit,
    onTrackClick: (trackId: String) -> Unit = { },
    onAlbumClick: (albumId: String) -> Unit = { }
) {
    val toolScaffoldState = rememberToolScaffoldState(onBackRequest)

    ColoredScaffold(
        state = coloredScaffoldState
    ) {
        ToolScaffold(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding()),
            state = toolScaffoldState,
            hazeState = hazeState
        ) { toolScaffoldInnerPadding ->

            FlingScrollScaffold(
                modifier = Modifier
                    .hazeSource(hazeState)
                    .fillMaxSize()
                    .background(Color.Black),
                state = rememberFlingScaffoldState(
                    yFlingOffset = toolScaffoldInnerPadding.calculateTopPadding()
            ) {
                calcScrollState(toolScaffoldInnerPadding.calculateTopPadding())

                    toolScaffoldState.toolBarTitle.value = if (isHeaderSwiped.value.not()) {
                        state.artist.name
                    } else {
                        null
                    }
                },
                backgroundContent = {
                    if (currentItemIndex.value == 0) {
                        ArtistAvatarPager(
                            pagerState = pagerState,
                            currentOffset = currentOffset,
                            screenHeight = screenHeight,
                            alpha = alpha,
                            artist = state.artist
                        )
                    }
                },
                headingContent = {
                    Header(
                        artist = state.artist,
                        screenHeight = screenHeight,
                        alpha = alpha,
                        state.artist.imagesUrl.size,
                        pagerState = pagerState
                    )
                }
            ) {
                Content(
                    artist = state.artist,
                    latestRelease = state.latestRelease,
                    topTracks = state.topTracks,
                    albums = state.albums,
                    singles = state.singles,
                    bottomPadding = bottomPadding,
                    onTrackClick = onTrackClick,
                    onReleaseClick = onAlbumClick
                )
            }
        }
    }
}

@Composable
private fun ArtistAvatarPager(
    artist: Artist,
    pagerState: PagerState,
    currentOffset: State<Dp>,
    screenHeight: Dp,
    alpha: FloatState
) {
    Box(
        modifier = Modifier
            .alpha(alpha.floatValue)
            .fillMaxWidth()
            .height(screenHeight * .7f)
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()

                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color.Transparent,
                        )
                    ),
                    blendMode = BlendMode.DstIn
                )
            }
            .offset {
                return@offset IntOffset(0, (-currentOffset.value / 4).roundToPx())
            }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
        ) { page ->
            AsyncImage(
                model = artist.imagesUrl[page],
                contentDescription = "Artist avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}

@Composable
private fun Header(
    artist: Artist,
    screenHeight: Dp,
    alpha: FloatState,
    imagesCount: Int,
    pagerState: PagerState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .height(screenHeight * TOP_PART_WEIGHT),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(alpha.floatValue)
                .align(Alignment.BottomCenter)
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (imagesCount > 1) {
                CirclePagerIndicator(
                    Modifier,
                    imagesCount,
                    pagerState
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Headset,
                    contentDescription = "headphones icon",
                    modifier = Modifier
                        .size(15.dp),
                    tint = Color.White
                )

                Text(
                    text = artist.statistics?.playCount.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.W600,
                    lineHeight = 10.sp
                )
            }

            Text(
                text = artist.name,
                fontWeight = FontWeight.W800,
                fontSize = 40.sp,
                lineHeight = 40.sp,
                color = Color.White
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                artist.genres.take(3).forEach {
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(Color.Black)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = it,
                            fontWeight = FontWeight.W700,
                            fontSize = 14.sp,
                            color = Color.White.copy(.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Content(
    artist: Artist,
    bottomPadding: Dp,
    latestRelease: Release,
    topTracks: List<Track>,
    albums: List<Release>,
    singles: List<Release>,
    onTrackClick: (trackId: String) -> Unit,
    onReleaseClick: (albumId: String) -> Unit
) {
    val screenHeight = LocalWindowInfo.current.containerDpSize.height

    Box(
        modifier = Modifier
            .heightIn(min = screenHeight)
    ) {
        Column {
            Spacer(Modifier.height(40.dp))

            LatestRelease(
                release = latestRelease,
                onReleaseClick
            )

            Spacer(Modifier.height(15.dp))

            if (topTracks.isNotEmpty()) {
                TopTracks(
                    tracks = topTracks,
                    onTrackClick = onTrackClick
                )
            }

            Spacer(Modifier.height(15.dp))

            if (albums.isNotEmpty()) {
                Section(
                    label = stringResource(R.string.albums, artist.name),
                    items = albums
                ) {
                    ReleaseMini(onReleaseClick, it)
                }
            }

            Spacer(Modifier.height(15.dp))

            if (singles.isNotEmpty()) {
                Section(
                    label = stringResource(R.string.singles, artist.name),
                    items = singles,
                ) {
                    ReleaseMini(onReleaseClick, it)
                }
            }

            Spacer(Modifier.height(bottomPadding))
            Spacer(Modifier.height(120.dp))

        }
    }
}

@Composable
fun LatestRelease(
    release: Release,
    onReleaseClick: (releaseId: String) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.latest_release),
            fontSize = 24.sp,
            fontWeight = FontWeight.W700,
            modifier = Modifier
                .padding(start = 25.dp)
        )

        Spacer(Modifier.height(15.dp))

        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    onReleaseClick(release.id)
                }
                .padding(start = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AsyncImage(
                model = release.coverArtUrl,
                contentDescription = "latest release image",
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .height(80.dp)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )

            Column {
                Text(
                    text = release.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W700
                )

                /*Text(
                    text = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(release.releaseDate),
                    fontSize = 14.sp
                )*/
            }
        }
    }
}

@Composable
private fun TopTracks(
    tracks: List<Track>,
    onTrackClick: (trackId: String) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.popular_tracks),
            fontSize = 24.sp,
            fontWeight = FontWeight.W700,
            modifier = Modifier
                .padding(start = 25.dp)
        )

        Spacer(Modifier.height(15.dp))

        for (track in tracks) {
            TrackMiniWithImage(
                modifier = Modifier
                    .padding(start = 20.dp, end = 10.dp)
                    .padding(vertical = 5.dp),
                track = track,
                onPrimaryColor = Color.White,
                onClick = { onTrackClick(it.id) }
            )
        }
    }
}

private fun FlingScrollScaffoldState.calcScrollState(
    topPadding: Dp
) {
    isHeaderVisible.value = lazyListState.firstVisibleItemIndex == 0
    totalHeight.value = screenHeight * TOP_PART_WEIGHT

    if (isHeaderVisible.value) {
        currentOffset.value = with(density) { lazyListState.firstVisibleItemScrollOffset.toDp() }

        alpha.floatValue = (totalHeight.value - topPadding - currentOffset.value) / totalHeight.value
        colorAlpha.floatValue = (totalHeight.value - topPadding - currentOffset.value) / 45.dp
    }
}