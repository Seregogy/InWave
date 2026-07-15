package com.inwave.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.command.LikeRepository
import com.inwave.player.state.PlayerState
import com.inwave.player.state.PlayerStateSource
import com.inwave.tool.ImagePaletteExtractor
import com.inwave.tool.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val playerStateSource: PlayerStateSource,
    val imagePaletteExtractor: ImagePaletteExtractor,
    private val likeRepository: LikeRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    init {
        viewModelScope.launch {
            playerStateSource.currentTrack.collect { track ->
                track?.coverArtUrl?.let {
                    imagePaletteExtractor.fetchImageByUrl(it)
                }
            }
        }
    }

    val track: StateFlow<Track?> = playerStateSource.currentTrack
    val playlist: StateFlow<List<Track>> = playerStateSource.playlist

    val currentPosition: StateFlow<Long> = playerStateSource.currentPosition
    val trackDuration: StateFlow<Long> = playerStateSource.currentTrackDuration

    val isLastTrack: StateFlow<Boolean> = playerStateSource.isLastTrack
    val isPlaying: StateFlow<Boolean> = playerStateSource.isPlaying

    val playerState: StateFlow<PlayerState> = playerStateSource.currentState
    val repeatMode: StateFlow<PlayerState.RepeatMode> = playerStateSource.currentRepeatModeState

    private val _isCurrentTrackLiked = MutableStateFlow(false)
    val isCurrentTrackLiked: StateFlow<Boolean> = _isCurrentTrackLiked

    fun like() {
        viewModelScope.launch {
            tokenManager.getToken()?.let { token ->
                track.value?.let { track ->
                    likeRepository.toggleLikeToTrack(token, track.id).onSuccess {
                        val message = if (it) "Добавлено в понравившиеся треки" else "Убрано из понравившихся треков"
                        Toast.makeText(context, message, Toast.LENGTH_LONG)
                            .show()
                    }.onFailure {
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

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

    suspend fun fetchCurrentTrackWithLyrics() {
        Log.d("PlayerStateSource", "start lrc fetch")

        playerStateSource.fetchCurrentTrackWithLyrics()
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