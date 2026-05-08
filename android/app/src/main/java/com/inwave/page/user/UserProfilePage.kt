// android/app/src/main/java/com/inwave/page/user/UserProfilePage.kt
package com.inwave.page.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.inwave.R
import com.inwave.control.mini.TrackMiniWithImage
import com.inwave.control.scaffold.ErrorDrawer
import com.inwave.control.scaffold.fling.FlingScrollScaffold
import com.inwave.control.scaffold.fling.rememberFlingScaffoldState
import com.inwave.control.scaffold.tool.ToolScaffold
import com.inwave.control.scaffold.tool.rememberToolScaffoldState
import com.inwave.domain.entity.User

private const val TOP_PART_WEIGHT = .55f

@Composable
fun UserProfilePage(
    userId: String,
    innerPadding: PaddingValues,
    bottomPadding: Dp,
    onBackRequest: () -> Unit = {},
    onTrackClick: (trackId: String) -> Unit = {},
    onPlaylistClick: (playlistId: String) -> Unit = {},
    onArtistClick: (artistId: String) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.loadUserProfile(userId)
    }

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
                onPlaylistClick = onPlaylistClick,
                onArtistClick = onArtistClick,
                onSettingsClick = onSettingsClick
            )
        }
        is UserProfilePageState.Error -> {
            ErrorDrawer(
                modifier = Modifier.fillMaxSize(),
                exception = currentState.exception
            )
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
    onPlaylistClick: (playlistId: String) -> Unit,
    onArtistClick: (artistId: String) -> Unit,
    onSettingsClick: () -> Unit
) {
    val toolScaffoldState = rememberToolScaffoldState(onBackRequest)

    ToolScaffold(
        modifier = Modifier
            .padding(top = innerPadding.calculateTopPadding()),
        state = toolScaffoldState,
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings),
                    tint = Color.White
                )
            }
        }
    ) { toolScaffoldInnerPadding ->

        FlingScrollScaffold(
            modifier = Modifier
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
                playlists = state.playlists,
                topArtists = state.topArtists,
                recentlyPlayed = state.recentlyPlayed,
                bottomPadding = bottomPadding,
                onTrackClick = onTrackClick,
                onPlaylistClick = onPlaylistClick,
                onArtistClick = onArtistClick
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
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Profile background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
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
            // Аватар
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

            // Имя пользователя
            Text(
                text = user.name,
                fontWeight = FontWeight.W800,
                fontSize = 40.sp,
                lineHeight = 40.sp,
                color = Color.White
            )

            // Статус аутентификации
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
    playlists: List<com.inwave.domain.entity.Playlist>,
    topArtists: List<com.inwave.domain.entity.Artist>,
    recentlyPlayed: List<com.inwave.domain.entity.Track>,
    bottomPadding: Dp,
    onTrackClick: (trackId: String) -> Unit,
    onPlaylistClick: (playlistId: String) -> Unit,
    onArtistClick: (artistId: String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Недавно прослушанные треки
        if (recentlyPlayed.isNotEmpty()) {
            Text(
                text = stringResource(R.string.recently_played),
                fontSize = 24.sp,
                fontWeight = FontWeight.W700,
                modifier = Modifier.padding(start = 25.dp)
            )

            Spacer(Modifier.height(15.dp))

            recentlyPlayed.take(5).forEach { track ->
                TrackMiniWithImage(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 10.dp)
                        .padding(vertical = 5.dp),
                    track = track,
                    onPrimaryColor = Color.White,
                    onClick = { onTrackClick(track.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Топ артисты
        if (topArtists.isNotEmpty()) {
            Text(
                text = stringResource(R.string.top_artists),
                fontSize = 24.sp,
                fontWeight = FontWeight.W700,
                modifier = Modifier.padding(start = 25.dp)
            )

            Spacer(Modifier.height(15.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(topArtists) { artist ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(75.dp)
                            .clickable { onArtistClick(artist.id) }
                    ) {
                        AsyncImage(
                            model = artist.imagesUrl.firstOrNull(),
                            contentDescription = artist.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(65.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = artist.name,
                            fontSize = 12.sp,
                            color = Color.White,
                            maxLines = 1,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Плейлисты
        if (playlists.isNotEmpty()) {
            Text(
                text = stringResource(R.string.playlists),
                fontSize = 24.sp,
                fontWeight = FontWeight.W700,
                modifier = Modifier.padding(start = 25.dp)
            )

            Spacer(Modifier.height(15.dp))

            playlists.forEach { playlist ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPlaylistClick(playlist.id) }
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (playlist.coverArtUrl != null) {
                            AsyncImage(
                                model = playlist.coverArtUrl,
                                contentDescription = playlist.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = playlist.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        Text(
                            text = "${playlist.tracksCount} tracks",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(bottomPadding))
        Spacer(modifier = Modifier.height(120.dp))
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