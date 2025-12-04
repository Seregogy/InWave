package com.inwave.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inwave.player.AudioPlayer
import com.inwave.tool.ImagePaletteExtractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    val audioPlayer: AudioPlayer,
    val imagePaletteExtractor: ImagePaletteExtractor
) : ViewModel() {
    init {
        Log.d("PLAYER", audioPlayer.toString())

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