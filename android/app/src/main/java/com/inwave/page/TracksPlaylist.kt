package com.inwave.page

import android.Manifest
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.inwave.control.mini.TrackMiniWithImage
import com.inwave.control.scaffold.fling.FlingScrollScaffold
import com.inwave.control.scaffold.fling.FlingScrollScaffoldState
import com.inwave.control.scaffold.fling.rememberFlingScaffoldState
import com.inwave.viewmodel.TracksPlaylistPageState
import com.inwave.viewmodel.TracksPlaylistViewModel

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun TracksPlaylist(
    innerPadding: PaddingValues,
    viewModel: TracksPlaylistViewModel = hiltViewModel()
) {
    val playlistState by remember { viewModel.tracksState }

    val permissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.READ_MEDIA_AUDIO)
    } else {
        rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

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
            ) { }
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
                    text = "Треки на устройстве",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.W600,
                    color = Color.White
                )
            }
        }
    ) {
        when {
            permissionState.status == PermissionStatus.Granted -> {
                when(val currentState = playlistState) {
                    is TracksPlaylistPageState.Idle -> { }
                    is TracksPlaylistPageState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is TracksPlaylistPageState.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Ой! Что-то пошло не так =(\n${currentState.message}", color = Color.White.copy(.7f))
                        }
                    }
                    is TracksPlaylistPageState.Success -> {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            currentState.tracks.forEachIndexed { index, it ->
                                TrackMiniWithImage(
                                    modifier = Modifier
                                        .padding(vertical = 5.dp)
                                        .padding(start = 20.dp, end = 10.dp)
                                        .padding(vertical = 5.dp),
                                    track = it,
                                    onClick = {
                                        viewModel.launchPlaylist(index)
                                    }
                                )
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
                        text = "Кажется, нет разрешения на просмотр аудио файлов в системе",
                        color = Color.White.copy(.7f)
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