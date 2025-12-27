package com.inwave.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inwave.domain.entity.Track
import com.inwave.player.state.PlayerState
import com.inwave.player.state.PlayerStateSource
import com.inwave.tool.ImagePaletteExtractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    private val playerStateSource: PlayerStateSource,
    val imagePaletteExtractor: ImagePaletteExtractor
) : ViewModel() {
    init {
        viewModelScope.launch {
            playerStateSource.currentTrack.collect { track ->
                track?.imageUrl?.let {
                    imagePaletteExtractor.fetchImageByUrl(it)
                }
            }
        }
    }

    val track: StateFlow<Track?> = playerStateSource.currentTrack

    val currentPosition: StateFlow<Long> = playerStateSource.currentPosition
    val trackDuration: StateFlow<Long> = playerStateSource.currentTrackDuration

    val isLastTrack: StateFlow<Boolean> = playerStateSource.isLastTrack
    val isPlaying: StateFlow<Boolean> = playerStateSource.isPlaying

    val playerState: StateFlow<PlayerState> = playerStateSource.currentState
    val repeatMode: StateFlow<PlayerState.RepeatMode> = playerStateSource.currentRepeatModeState

    fun playPause() {
        viewModelScope.launch {
            playerStateSource.playPause()
        }
    }

    fun seekToNext() {
        viewModelScope.launch {
            playerStateSource.seekToNext()
        }
    }

    fun seekToPrev() {
        viewModelScope.launch {
            playerStateSource.seekToPrev()
        }
    }

    fun nextRepeatMode() {
        viewModelScope.launch {
            playerStateSource.nextRepeatMode()
        }
    }

    fun seek(position: Long) {
        viewModelScope.launch {
            playerStateSource.seek(position)
        }
    }

    override fun onCleared() {
        releasePlayer()
    }

    fun releasePlayer() {
        viewModelScope.launch {
            playerStateSource.release()
        }
    }
}