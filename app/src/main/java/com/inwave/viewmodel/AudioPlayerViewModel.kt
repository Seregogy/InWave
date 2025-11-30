package com.inwave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inwave.player.AudioPlayer
import com.inwave.tool.ImagePaletteExtractor
import kotlinx.coroutines.launch
import javax.inject.Inject

class AudioPlayerViewModel @Inject constructor(
    val audioPlayer: AudioPlayer,
    val imagePaletteExtractor: ImagePaletteExtractor
) : ViewModel() {
    init {
        viewModelScope.launch {
            audioPlayer.currentPlayerTrack.collect { track ->
                track?.data?.imageUrl?.let {
                    imagePaletteExtractor.fetchImageByUrl(it)
                }
            }
        }
    }

    override fun onCleared() {
        audioPlayer.release()
    }
}