package com.inwave.page

import android.Manifest
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.inwave.R
import com.inwave.control.flingscroll.FlingScrollScaffold
import com.inwave.control.flingscroll.FlingScrollScaffoldState
import com.inwave.control.flingscroll.rememberFlingScaffoldState
import com.inwave.viewmodel.TracksPlaylistViewModel
import com.inwave.viewmodel.UiState
import kotlin.collections.forEach

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun TracksPlaylist(
    innerPadding: PaddingValues,
    viewModel: TracksPlaylistViewModel = hiltViewModel()
) {
    val tracksState by viewModel.tracksState

    val permissionState = rememberPermissionState(Manifest.permission.READ_MEDIA_AUDIO)
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()

        when {
            permissionState.status == PermissionStatus.Granted -> {
                viewModel.loadTracks()
            }
        }
    }

    FlingScrollScaffold(
        modifier = Modifier
            .background(Color.Black),
        state = rememberFlingScaffoldState(
            yFlingOffset = innerPadding.calculateTopPadding()
        ) {
            calcScrollState(innerPadding.calculateTopPadding())
        },
        backgroundContent = {
            Box(
                modifier = Modifier
                    .alpha(alpha.floatValue)
                    .fillMaxWidth()
                    .height(screenHeight * .7f)
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                    .offset {
                        return@offset IntOffset(0, (-currentOffset.value / 4).roundToPx())
                    }
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
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = painterResource(R.drawable.inwave_logo),
                    contentDescription = "Background image",
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
            }
        },
        headingContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .height(screenHeight * .5f)
                    .alpha(alpha.floatValue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Local stored music",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.W600
                )
            }
        }
    ) {
        when {
            permissionState.status == PermissionStatus.Granted -> {
                when(tracksState.state) {
                    UiState.State.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    UiState.State.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Ой! Что-то пошло не так =(\n${tracksState.error}")
                        }
                    }
                    UiState.State.Success -> {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            tracksState.data!!.forEach { track ->
                                Row(
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.small)
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .fillMaxSize()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = track.name,
                                            fontWeight = FontWeight.W600,
                                            overflow = TextOverflow.Clip,
                                            maxLines = 1
                                        )

                                        Text(
                                            text = track.album?.artists?.joinToString(", ") { it.name }
                                                ?: "unknown"
                                        )
                                    }

                                    Text(
                                        text = "${track.duration} ms"
                                    )
                                }
                            }
                        }
                    }
                }
            }
            else -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 40.dp),
                        text = "Кажется, нет разрешения на просмотр аудио файлов в системе"
                    )
                }
            }
        }
    }
}

private fun FlingScrollScaffoldState.calcScrollState(
    topPadding: Dp
) {
    isHeaderVisible.value = lazyListState.firstVisibleItemIndex == 0
    totalHeight.value = screenHeight * .5f

    if (isHeaderVisible.value) {
        currentOffset.value = with(density) { lazyListState.firstVisibleItemScrollOffset.toDp() }

        alpha.floatValue = (totalHeight.value - topPadding - currentOffset.value) / totalHeight.value
        colorAlpha.floatValue = (totalHeight.value - topPadding - currentOffset.value) / 45.dp
    }
}