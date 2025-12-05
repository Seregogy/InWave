package com.inwave.player.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.inwave.R
import com.inwave.control.TrackControl
import com.inwave.player.state.PlayerCommand
import com.inwave.viewmodel.AudioPlayerViewModel

@Composable
fun MiniAudioPlayer(
    viewModel: AudioPlayerViewModel,
    modifier: Modifier = Modifier,
    onExpandRequest: () -> Unit
) {
    val context = LocalContext.current
    val track by viewModel.playerStateSource.currentTrack.collectAsStateWithLifecycle()
    val currentState by viewModel.playerStateSource.currentCommand.collectAsStateWithLifecycle()

    val isPlay by remember {
        derivedStateOf {
            currentState == PlayerCommand.Play()
        }
    }

    track?.let {
        TrackControl(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp),
            onClick = { onExpandRequest() },
            track = it
        ) {
            Row(
                modifier = Modifier
                    .weight(2f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { }
                ) {
                    Icon(
                        imageVector = if (false)
                            Icons.Rounded.Favorite
                        else
                            Icons.Rounded.FavoriteBorder,
                        contentDescription = "favorite icon button",
                        modifier = Modifier
                            .size(24.dp),
                        tint = Color.White.copy(.7f)
                    )
                }

                IconButton(
                    onClick = { }
                ) {
                    Icon(
                        imageVector = if (isPlay)
                            Icons.Rounded.Pause
                        else
                            Icons.Rounded.PlayArrow,
                        contentDescription = "play/pause icon",
                        modifier = Modifier
                            .size(26.dp),
                        tint = Color.White.copy(.7f)
                    )
                }
            }
        }
    }
}