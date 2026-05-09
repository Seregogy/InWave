package com.inwave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inwave.di.RemoteRepo
import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track
import com.inwave.domain.usecase.artist.query.GetArtistTopTracksUseCase
import com.inwave.domain.usecase.artist.query.GetTopArtistsUseCase
import com.inwave.domain.usecase.release.query.GetTopReleasesUseCase
import com.inwave.player.AudioVisualizer
import com.inwave.player.state.PlayerStateSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MainPageViewModelState() {
    object Idle: MainPageViewModelState()
    object Loading: MainPageViewModelState()
    class Success(
        val topArtistsAndTracks: List<Pair<Artist, List<Track>>>,
        val topReleases: List<Release>,
    ): MainPageViewModelState()
    class Error(val exception: Throwable): MainPageViewModelState()
}

@HiltViewModel
class MainPageViewModel @Inject constructor(
    private val visualizer: AudioVisualizer,
    private val playerStateSource: PlayerStateSource,
    @RemoteRepo private val getTopArtistsUseCase: GetTopArtistsUseCase,
    @RemoteRepo private val getArtistTopTracksUseCase: GetArtistTopTracksUseCase,
    @RemoteRepo private val getTopReleasesUseCase: GetTopReleasesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<MainPageViewModelState>(MainPageViewModelState.Idle)
    val state: StateFlow<MainPageViewModelState> = _state

    val wave = visualizer.wavePulse

    init {
        playerStateSource.start()
        viewModelScope.launch {
            loadMainPage()
        }
    }

    fun startListener() {
        visualizer.startListener()
    }

    fun pauseListener() {
        visualizer.pauseListener()

    }

    suspend fun loadMainPage() {
        _state.emit(MainPageViewModelState.Loading)

        _state.emit(
            runCatching {
                val topArtists = getTopArtistsUseCase(5).getOrNull()?.map {
                    it to getArtistTopTracksUseCase(it.id, 5).getOrElse { listOf() }
                } ?: error("failure to fetch top artists")

                val topReleases = getTopReleasesUseCase(5).getOrNull()
                    ?: error("failure to fetch top releases")

                MainPageViewModelState.Success(topArtists, topReleases)
            }.fold(
                onSuccess = { MainPageViewModelState.Success(it.topArtistsAndTracks, it.topReleases) },
                onFailure = { MainPageViewModelState.Error(it) }
            )
        )
    }
}