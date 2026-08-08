// android/app/src/main/java/com/inwave/page/user/UserProfilePage.kt
package com.inwave.page.user

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fitInside
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.inwave.R
import com.inwave.control.Section
import com.inwave.control.mini.ReleaseMini
import com.inwave.control.mini.TrackMiniWithImage
import com.inwave.control.scaffold.ErrorDrawer
import com.inwave.control.scaffold.fling.FlingScrollScaffold
import com.inwave.control.scaffold.fling.FlingScrollScaffoldState
import com.inwave.control.scaffold.fling.rememberFlingScaffoldState
import com.inwave.control.scaffold.tool.ToolScaffold
import com.inwave.control.scaffold.tool.rememberToolScaffoldState
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track
import com.inwave.domain.entity.User
import com.inwave.viewmodel.UserProfilePageState
import com.inwave.viewmodel.UserProfileViewModel
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import kotlin.random.Random

private const val TOP_PART_WEIGHT = .55f

@Composable
fun UserProfilePageRefreshable(
    innerPadding: PaddingValues,
    bottomPadding: Dp,
    onBackRequest: () -> Unit = {},
    onTrackClick: (trackId: String) -> Unit = {},
    onReleaseClick: (releaseId: String) -> Unit = {},
    onLoginClick: () -> Unit = {},
    viewModel: UserProfileViewModel = hiltViewModel()
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
                viewModel.loadUserProfile()
            }
        }
    ) {
        UserProfilePage(
            innerPadding = innerPadding,
            bottomPadding = bottomPadding,
            onBackRequest = onBackRequest,
            onTrackClick = onTrackClick,
            onReleaseClick = onReleaseClick,
            onLoginClick = onLoginClick,
            viewModel = viewModel
        )
    }
}

@Composable
fun UserProfilePage(
    innerPadding: PaddingValues,
    bottomPadding: Dp,
    onBackRequest: () -> Unit = {},
    onTrackClick: (trackId: String) -> Unit = {},
    onReleaseClick: (releaseId: String) -> Unit = {},
    onLoginClick: () -> Unit = {},
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val currentState = state) {
        is UserProfilePageState.Idle -> {}
        is UserProfilePageState.Loading -> UserProfileSkeleton()
        is UserProfilePageState.Success -> {
            DrawUserProfilePage(
                state = currentState,
                innerPadding = innerPadding,
                bottomPadding = bottomPadding,
                onBackRequest = onBackRequest,
                onTrackClick = onTrackClick,
                onReleaseClick = onReleaseClick,
            )
        }
        is UserProfilePageState.Error -> {
            ErrorDrawer(
                modifier = Modifier.fillMaxSize(),
                throwable = currentState.exception
            ) {
                TextButton(
                    onClick = onLoginClick
                ) {
                    Text(stringResource(R.string.on_login_page))
                }
            }
        }
    }
}

@Composable
private fun DrawUserProfilePage(
    state: UserProfilePageState.Success,
    innerPadding: PaddingValues,
    bottomPadding: Dp,
    onBackRequest: () -> Unit,
    onTrackClick: (trackId: String) -> Unit,
    onReleaseClick: (releaseId: String) -> Unit,
) {
    val toolScaffoldState = rememberToolScaffoldState(onBackRequest)
    val topBarHazeState = rememberHazeState()

    ToolScaffold(
        modifier = Modifier
            .padding(top = innerPadding.calculateTopPadding()),
        state = toolScaffoldState,
        hazeState = topBarHazeState,
    ) { toolScaffoldInnerPadding ->

        FlingScrollScaffold(
            modifier = Modifier
                .hazeSource(state = topBarHazeState)
                .fillMaxSize()
                .background(Color.Black),
            state = rememberFlingScaffoldState(
                yFlingOffset = toolScaffoldInnerPadding.calculateTopPadding()
            ) {
                calcScrollState(toolScaffoldInnerPadding.calculateTopPadding())

                toolScaffoldState.toolBarTitle.value = if (isHeaderSwiped.value.not()) {
                    state.user.name
                } else {
                    null
                }
            },
            backgroundContent = {
                if (currentItemIndex.value == 0) {
                    ProfileBackground(
                        avatarUrl = state.user.avatarUrl,
                        currentOffset = currentOffset,
                        screenHeight = screenHeight,
                        alpha = alpha
                    )
                }
            },
            headingContent = {
                ProfileHeader(
                    user = state.user,
                    screenHeight = screenHeight,
                    alpha = alpha
                )
            }
        ) {
            ProfileContent(
                user = state.user,
                likedTracks = state.likedTracks,
                likedReleases = state.likedReleases,
                bottomPadding = bottomPadding,
                onTrackClick = onTrackClick,
                onReleaseClick = onReleaseClick
            )
        }
    }
}

@Composable
private fun ProfileBackground(
    avatarUrl: String,
    currentOffset: State<Dp>,
    screenHeight: Dp,
    alpha: State<Float>
) {
    Box(
        modifier = Modifier
            .alpha(alpha.value)
            .fillMaxWidth()
            .height(screenHeight * .7f)
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
            .offset {
                IntOffset(0, (-currentOffset.value / 4).roundToPx())
            }
    ) {
        Box {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Profile background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(100.dp)
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    user: User,
    screenHeight: Dp,
    alpha: State<Float>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight * TOP_PART_WEIGHT),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(alpha.value)
                .padding(horizontal = 10.dp)
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Text(
                text = user.name,
                fontWeight = FontWeight.W800,
                fontSize = 40.sp,
                lineHeight = 40.sp,
                color = Color.White
            )

            if (user.isAuthenticated) {
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(Color(0xFF1DB954).copy(alpha = 0.3f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Verified",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W600,
                        color = Color(0xFF1DB954)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    user: User,
    likedTracks: List<Track>,
    likedReleases: List<Release>,
    bottomPadding: Dp,
    onTrackClick: (trackId: String) -> Unit,
    onReleaseClick: (releaseId: String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .animateContentSize()
                .clip(MaterialTheme.shapes.small)
                .clickable { }
                .background(Color.White.copy(.07f))
                .padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Text(
                text = stringResource(R.string.about_me),
                fontSize = 16.sp,
                fontWeight = FontWeight.W700
            )

            Text(
                text = LoremIpsum(100).values.joinToString(),
                maxLines = 3,
                style = TextStyle(
                    color = Color.White.copy(.7f),
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        val rand = remember {
            Random(228)
        }

        val currentDate = remember {
            LocalDate.now()
        }

        val activity = remember {
            val startDate = currentDate.run {
                LocalDate.of(year, month, 1)
            }

            val endDate = currentDate.run {
                LocalDate.of(year, month, lengthOfMonth())
            }

            startDate.datesUntil(endDate.plusDays(1))
                .toList()
                .associateWith { (rand.nextInt(0, 10) - 5).coerceIn(0..5) }
        }

        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(horizontal = 30.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(Color.White.copy(.07f))
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Text(
                    text = stringResource(R.string.activity),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W700
                )

                ActivityMonth(
                    now = currentDate
                ) {
                    activity.values.forEach {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (it == 0)
                                        Color(30, 30, 30)
                                    else
                                        Color(0, 50 + ((it / 5.toFloat()) * 100).toInt(), 0)
                                )
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(MaterialTheme.shapes.small)
                    .background(Color.White.copy(.07f))
                    .padding(15.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Text(
                    text = stringResource(R.string.hours_amount_of_music),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W700
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight(),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(
                        text = "7ч. 38мин.",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.W700
                    )

                    /*Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CircleShape)
                    ) {
                        Box(Modifier.weight(2f).height(20.dp).background(Color(0xFF4EAD4E)))
                        Box(Modifier.weight(1f).height(20.dp).background(Color(0xFFBB467D)))
                        Box(Modifier.weight(3f).height(20.dp).background(Color(0xFFBFCB1A)))
                    }*/
                }
            }
        }

        Spacer(Modifier.height(25.dp))

        if (likedTracks.isNotEmpty()) {
            Text(
                text = stringResource(R.string.liked_tracks),
                fontSize = 24.sp,
                fontWeight = FontWeight.W700,
                modifier = Modifier.padding(start = 30.dp)
            )

            Spacer(Modifier.height(15.dp))

            likedTracks.forEach { track ->
                TrackMiniWithImage(
                    modifier = Modifier
                        .padding(start = 30.dp, end = 15.dp)
                        .padding(vertical = 5.dp),
                    track = track,
                    onPrimaryColor = Color.White,
                    onClick = { onTrackClick(track.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        if (likedReleases.isNotEmpty()) {
            Spacer(Modifier.height(15.dp))

            Section(
                label = stringResource(R.string.liked_releases),
                items = likedReleases,
            ) {
                ReleaseMini(onReleaseClick, it)
            }
        }

        Spacer(modifier = Modifier.height(bottomPadding))
        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
fun ActivityMonth(
    modifier: Modifier = Modifier,
    now: LocalDate = LocalDate.now(),
    spaceBetween: Dp = 5.dp,
    content: @Composable () -> Unit
) {
    val spaceBetweenPx = with(LocalDensity.current) {
        spaceBetween.roundToPx()
    }

    val firstDateOfMonth = now.run {
        LocalDate.of(year, month, 1)
    }

    val lastDateOfMonth = now.run {
        LocalDate.of(year, month, now.lengthOfMonth())
    }

    val daysOfMonth = firstDateOfMonth
        .datesUntil(lastDateOfMonth.plusDays(1))
        .toList()

    var lastSunday = -1
    var rowsCount = daysOfMonth.count {
        if (it.dayOfWeek.value == 7) {
            lastSunday = it.dayOfMonth

            true
        } else {
            false
        }
    }

    if (lastSunday != now.lengthOfMonth()) rowsCount++

    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val placeables = measurables.take(now.lengthOfMonth()).map { measurable ->
            measurable.measure(constraints)
        }.zip(daysOfMonth)

        val totalWidth = (placeables.first().first.width * 7) + (spaceBetweenPx * 6)
        val totalHeight = (placeables.first().first.height * rowsCount) + (spaceBetweenPx * (rowsCount - 2))

        var weekIndex = 0

        layout(
            width = totalWidth,
            height = totalHeight
        ) {
            var xPosition: Int
            var yPosition: Int

            placeables.forEach { (placeable, date) ->
                xPosition = (placeable.width * (date.dayOfWeek.value - 1)) + (spaceBetweenPx * (date.dayOfWeek.value - 1))
                yPosition = (placeable.height * weekIndex) + (spaceBetweenPx * (weekIndex - 1))

                placeable.placeRelative(
                    xPosition,
                    yPosition
                )

                if (date.dayOfWeek.value == 7) weekIndex++
            }
        }
    }
}

private fun FlingScrollScaffoldState.calcScrollState(topPadding: Dp) {
    isHeaderVisible.value = lazyListState.firstVisibleItemIndex == 0
    totalHeight.value = screenHeight * TOP_PART_WEIGHT

    if (isHeaderVisible.value) {
        currentOffset.value = with(density) { lazyListState.firstVisibleItemScrollOffset.toDp() }
        alpha.floatValue = (totalHeight.value - topPadding - currentOffset.value) / totalHeight.value
        colorAlpha.floatValue = (totalHeight.value - topPadding - currentOffset.value) / 45.dp
    }
}