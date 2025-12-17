package com.inwave.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inwave.domain.entity.Track
import com.inwave.domain.usecase.track.GetAllTracksUseCase
import com.inwave.player.state.PlayerStateSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

sealed class TracksPlaylistPageState() {
    class Idle: TracksPlaylistPageState()
    class Loading: TracksPlaylistPageState()
    class Success(val tracks: List<Track>): TracksPlaylistPageState()
    class Error(val message: String?): TracksPlaylistPageState()
}

@HiltViewModel
class TracksPlaylistViewModel @Inject constructor(
    val getAllTracksUseCase: GetAllTracksUseCase,
    private val playerStateSource: PlayerStateSource
) : ViewModel() {
    private val _tracksState = mutableStateOf<TracksPlaylistPageState>(TracksPlaylistPageState.Idle())
    val tracksState: State<TracksPlaylistPageState> = _tracksState

    suspend fun loadTracks() {
        runCatching {
            withTimeout(10000) {
                _tracksState.value = getAllTracksUseCase().fold(
                    onSuccess = {
                        TracksPlaylistPageState.Success(it)
                    },
                    onFailure = {
                        TracksPlaylistPageState.Error(it.message)
                    }
                )
            }
        }.onFailure {
            _tracksState.value = TracksPlaylistPageState.Error(it.message)
        }
    }

    fun launchPlaylist(tracks: List<Track>, startTrackIndex: Int = 0) {
        viewModelScope.launch {
            playerStateSource.setPlaylist(tracks)
            playerStateSource.seekToIndex(startTrackIndex)
        }
    }
}