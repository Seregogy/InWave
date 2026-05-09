// android/app/src/main/java/com/inwave/page/user/UserProfileViewModel.kt
package com.inwave.page.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inwave.di.RemoteRepo
import com.inwave.di.UserRemoteRepo
import com.inwave.domain.repository.query.UserQueryRepository
import com.inwave.domain.usecase.release.query.GetReleaseUseCase
import com.inwave.domain.usecase.track.query.GetTrackUseCase
import com.inwave.tool.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    @RemoteRepo private val getReleaseUseCase: GetReleaseUseCase,
    @RemoteRepo private val getTrackUseCase: GetTrackUseCase,
    @UserRemoteRepo private val userQueryRepository: UserQueryRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _state = MutableStateFlow<UserProfilePageState>(UserProfilePageState.Idle)
    val state: StateFlow<UserProfilePageState> = _state.asStateFlow()

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