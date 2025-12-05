package com.inwave.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inwave.domain.entity.Track
import com.inwave.domain.usecase.track.GetAllTracksUseCase
import com.inwave.player.state.PlayerStateSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState<T>(
    val state: State = State.Loading,
    val data: T? = null,
    val error: String? = null
) {
    enum class State {
        Loading,
        Success,
        Error
    }
}

@HiltViewModel
class TracksPlaylistViewModel @Inject constructor(
    val getAllTracksUseCase: GetAllTracksUseCase,
    private val playerStateSource: PlayerStateSource
) : ViewModel() {
    private val _tracksState = mutableStateOf(UiState<List<Track>>())
    val tracksState: State<UiState<List<Track>>> = _tracksState

    suspend fun loadTracks() {
        _tracksState.value = getAllTracksUseCase().fold(
            onSuccess = {
                UiState(
                    state = UiState.State.Success,
                    data = it
                )
            },
            onFailure = {
                UiState(
                    state = UiState.State.Error,
                    error = it.message
                )
            }
        )
    }

    fun launchTrack(path: String) {
        viewModelScope.launch {
            Log.d("PLAYER", path)
            playerStateSource.setPlaylist(listOf(path))
        }
    }
}