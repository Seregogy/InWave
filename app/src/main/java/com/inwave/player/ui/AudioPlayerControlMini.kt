package com.inwave.player.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inwave.player.AudioPlayer
import com.inwave.viewmodel.AudioPlayerViewModel

@Composable
fun MiniAudioPlayer(
    viewModel: AudioPlayerViewModel,
    modifier: Modifier = Modifier,
    onExpandRequest: () -> Unit
) {
    val currentTrack by viewModel.audioPlayer.currentPlayerTrack.collectAsStateWithLifecycle()
    val currentState by viewModel.audioPlayer.currentState.collectAsStateWithLifecycle()

    val isPlay by remember {
        derivedStateOf {
            currentState == AudioPlayer.AudioPlayerState.Play
        }
    }


}