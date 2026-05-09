package com.inwave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inwave.di.LocalRepo
import com.inwave.domain.entity.Track
import com.inwave.domain.usecase.track.query.GetAllTracksUseCase
import com.inwave.player.state.PlayerStateSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    @LocalRepo private val getAllTracksUseCase: GetAllTracksUseCase,
    private val playerStateSource: PlayerStateSource
) : ViewModel() {
    private val _tracksState = MutableStateFlow<TracksPlaylistPageState>(TracksPlaylistPageState.Idle())
    val tracksState: StateFlow<TracksPlaylistPageState> = _tracksState.asStateFlow()

    suspend fun loadTracks() {
        runCatching {
            withTimeout(10000) {
                _tracksState.value = getAllTracksUseCase().fold(
                    onSuccess = {
                        it.forEach { track ->
                            println(track.id)
                        }

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

    fun launchPlaylist(startTrackIndex: Int = 0) {
        viewModelScope.launch {
            when (val state = tracksState.value) {
                is TracksPlaylistPageState.Success -> {
                    playerStateSource.setPlaylist(state.tracks)
                    playerStateSource.seekToIndex(startTrackIndex)
                    playerStateSource.play()
                }
                else -> { }
            }
        }
    }
}