package com.inwave.player.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inwave.player.state.PlayerCommand
import com.inwave.viewmodel.AudioPlayerViewModel

@Composable
fun MiniAudioPlayer(
    viewModel: AudioPlayerViewModel,
    modifier: Modifier = Modifier,
    onExpandRequest: () -> Unit
) {
    val currentTrack by viewModel.playerStateSource.currentTrack.collectAsStateWithLifecycle()
    val currentState by viewModel.playerStateSource.currentCommand.collectAsStateWithLifecycle()

    val isPlay by remember {
        derivedStateOf {
            currentState == PlayerCommand.Play()
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth().height(50.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(currentTrack?.name ?: "unknown")
    }
}