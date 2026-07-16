package com.inwave.player.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inwave.control.TrackControl
import com.inwave.viewmodel.AudioPlayerViewModel

@Composable
fun MiniAudioPlayer(
    viewModel: AudioPlayerViewModel,
    modifier: Modifier = Modifier,
    onExpandRequest: () -> Unit
) {
    val track by viewModel.track.collectAsStateWithLifecycle()
    val isPlay by viewModel.isPlaying.collectAsStateWithLifecycle()

    val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()
    val duration by viewModel.trackDuration.collectAsStateWithLifecycle(1L)

    track?.let {
        TrackControl(
            modifier = modifier
                .padding(vertical = 7.dp)
                .fillMaxWidth(),
            onClick = { onExpandRequest() },
            track = it,
            trackTimelinePosition = if (duration > 0f) {
                (currentPosition.toFloat() / duration).coerceIn(0f, 1f)
            } else {
                0f
            }
        ) {
            Row(
                modifier = Modifier
                    .weight(2f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier
                        .heightIn(max = 40.dp),
                    onClick = { viewModel.like() }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = "favorite icon button",
                        modifier = Modifier
                            .size(24.dp),
                        tint = Color.White.copy(.7f)
                    )
                }

                IconButton(
                    modifier = Modifier
                        .heightIn(max = 40.dp),
                    onClick = {
                        viewModel.playPause()
                    }
                ) {
                    Icon(
                        imageVector = if (isPlay)
                            Icons.Rounded.Pause
                        else
                            Icons.Rounded.PlayArrow,
                        contentDescription = "play/pause icon",
                        modifier = Modifier
                            .size(24.dp),
                        tint = Color.White.copy(.7f)
                    )
                }
            }
        }
    }
}