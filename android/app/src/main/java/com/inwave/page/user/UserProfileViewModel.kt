// android/app/src/main/java/com/inwave/page/user/UserProfileViewModel.kt
package com.inwave.page.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inwave.di.UserRemoteRepo
import com.inwave.domain.repository.query.UserQueryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    @UserRemoteRepo private val userQueryRepository: UserQueryRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UserProfilePageState>(UserProfilePageState.Idle)
    val state: StateFlow<UserProfilePageState> = _state.asStateFlow()

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _state.value = UserProfilePageState.Loading

            userQueryRepository.getUser(userId)
                .onSuccess { user ->
                    _state.value = UserProfilePageState.Success(
                        user = user,
                        playlists = emptyList(),
                        topArtists = emptyList(),
                        recentlyPlayed = emptyList()
                    )
                }
                .onFailure { exception ->
                    _state.value = UserProfilePageState.Error(exception)
                }
        }
    }
}