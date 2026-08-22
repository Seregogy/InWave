package com.inwave.viewmodel

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inwave.di.RemoteRepo
import com.inwave.di.UserRemoteRepo
import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.command.UserCommandRepository
import com.inwave.domain.repository.query.UserQueryRepository
import com.inwave.domain.usecase.release.query.GetReleaseTracksUseCase
import com.inwave.domain.usecase.track.query.GetTracksUseCase
import com.inwave.player.state.PlayerStateSource
import com.inwave.tool.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MainViewModelState {
    object Idle: MainViewModelState()
    object Loading: MainViewModelState()
    object Offline: MainViewModelState()

    class Authorized(): MainViewModelState()
    class Unauthorized(): MainViewModelState()

    class Error(val exception: Throwable): MainViewModelState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    val playerStateSource: PlayerStateSource,
    val userRepository: UserCommandRepository,
    val tokenManager: TokenManager,
    private val connectivityManager: ConnectivityManager,
    @UserRemoteRepo val userQueryRepository: UserQueryRepository,
    @RemoteRepo val getTrackUseCase: GetTracksUseCase,
    @RemoteRepo val getReleaseTracksUseCase: GetReleaseTracksUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<MainViewModelState>(MainViewModelState.Idle)
    val state: StateFlow<MainViewModelState> = _state

    init {
        initialize()
    }

    fun isInternetAvailable(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun initialize() {
        viewModelScope.launch {
            runCatching {
                _state.value = MainViewModelState.Loading

                if (isInternetAvailable().not()) {
                    _state.value = MainViewModelState.Offline
                    return@launch
                }

                if (tokenManager.hasToken().not()) {
                    _state.value = MainViewModelState.Unauthorized()
                    return@launch
                }


                //TODO: добавить проверку на Unauthorized (сейчас падает даже если запрос не прошел из за сети)
                if (userQueryRepository.getUserByToken(tokenManager.getTokenForce()).isFailure) {
                    _state.value = MainViewModelState.Unauthorized()
                    return@launch
                }
            }.onSuccess {
                _state.value = MainViewModelState.Authorized()
            }.onFailure {
                _state.value = MainViewModelState.Error(it)
            }
        }
    }
}