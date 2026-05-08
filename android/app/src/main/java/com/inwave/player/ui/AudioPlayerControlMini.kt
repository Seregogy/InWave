package com.inwave.player.ui

import androidx.compose.foundation.layout.Arrangement
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
    val currentState by viewModel.playerState.collectAsStateWithLifecycle()
    val isPlay by viewModel.isPlaying.collectAsStateWithLifecycle()

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
                            .size(26.dp),
                        tint = Color.White.copy(.7f)
                    )
                }
            }
        }
    }
}