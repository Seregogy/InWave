package com.inwave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inwave.di.RemoteRepo
import com.inwave.di.UserRemoteRepo
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track
import com.inwave.domain.entity.User
import com.inwave.domain.repository.query.UserQueryRepository
import com.inwave.domain.usecase.release.query.GetReleaseUseCase
import com.inwave.domain.usecase.track.query.GetTrackUseCase
import com.inwave.tool.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UserProfilePageState {
    object Idle : UserProfilePageState()
    object Loading : UserProfilePageState()
    data class Success(
        val user: User,
        val likedTracks: List<Track>,
        val likedReleases: List<Release>,
    ) : UserProfilePageState()
    data class Error(val exception: Throwable) : UserProfilePageState()
}

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    @RemoteRepo private val getReleaseUseCase: GetReleaseUseCase,
    @RemoteRepo private val getTrackUseCase: GetTrackUseCase,
    @UserRemoteRepo private val userQueryRepository: UserQueryRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _state = MutableStateFlow<UserProfilePageState>(UserProfilePageState.Idle)
    val state: StateFlow<UserProfilePageState> = _state.asStateFlow()

    val isLoading = _state.map { it is UserProfilePageState.Loading }

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _state.value = UserProfilePageState.Loading

            runCatching {
                val token = tokenManager.getTokenForce()
                userQueryRepository.getUserByToken(token)
                    .onSuccess { user ->
                        //TODO: убрать ддос атаку на бек
                        val releases = user.likedReleases.map {
                            getReleaseUseCase(it).getOrThrow()
                        }

                        val tracks = user.likedTracks.map {
                            getTrackUseCase(it).getOrThrow()
                        }

                        _state.value = UserProfilePageState.Success(
                            user = user,
                            likedReleases = releases,
                            likedTracks = tracks
                        )
                    }
                    .onFailure { exception ->
                        _state.value = UserProfilePageState.Error(exception)
                        exception.printStackTrace()
                    }
            }.onFailure {
                _state.value = UserProfilePageState.Error(it)
                it.printStackTrace()
            }
        }
    }
}