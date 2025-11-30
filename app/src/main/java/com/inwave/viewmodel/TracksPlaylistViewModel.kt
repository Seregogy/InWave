package com.inwave.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.inwave.domain.entity.Track
import com.inwave.domain.usecase.track.GetAllTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val getAllTracksUseCase: GetAllTracksUseCase
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
}