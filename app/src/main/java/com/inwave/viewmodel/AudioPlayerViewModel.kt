package com.inwave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inwave.player.state.PlayerStateSource
import com.inwave.tool.ImagePaletteExtractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    val playerStateSource: PlayerStateSource,
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

    override fun onCleared() {
        playerStateSource.release()
    }
}