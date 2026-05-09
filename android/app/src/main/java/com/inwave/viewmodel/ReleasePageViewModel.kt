package com.inwave.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.inwave.di.RemoteRepo
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.command.LikeRepository
import com.inwave.domain.usecase.artist.query.GetArtistReleasesUseCase
import com.inwave.domain.usecase.release.query.GetReleaseTracksUseCase
import com.inwave.domain.usecase.release.query.GetReleaseUseCase
import com.inwave.tool.ImagePaletteExtractor
import com.inwave.tool.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ReleasePageViewModelState() {
    object Idle: ReleasePageViewModelState()
    object Loading: ReleasePageViewModelState()
    class Success(
        val tracks: List<Track>,
        val release: Release,
        val otherReleases: List<Release>
    ): ReleasePageViewModelState()
    class Error(val exception: Throwable): ReleasePageViewModelState()
}

@HiltViewModel
class ReleasePageViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val colorExtractor: ImagePaletteExtractor,
    @RemoteRepo private val getRelease: GetReleaseUseCase,
    @RemoteRepo private val getReleaseTracks: GetReleaseTracksUseCase,
    @RemoteRepo private val getArtistReleasesUseCase: GetArtistReleasesUseCase,
    private val likeRepository: LikeRepository,
    private val tokenManager: TokenManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val releaseId: String? = savedStateHandle["releaseId"]

    val palette: StateFlow<Palette?> = colorExtractor.palette
    val bitmap: StateFlow<Bitmap?> = colorExtractor.bitmap

    private val _state = MutableStateFlow<ReleasePageViewModelState>(ReleasePageViewModelState.Idle)
    val state: StateFlow<ReleasePageViewModelState> = _state

    init {
        viewModelScope.launch {
            loadRelease()
        }
    }

    fun like() {
        viewModelScope.launch {
            tokenManager.getToken()?.let { token ->
                releaseId?.let {
                    likeRepository.toggleLikeToRelease(token, releaseId).onSuccess {
                        val message = if (it) "Добавлено в понравившиеся релизы" else "Убрано из понравившихся релизов"
                        Toast.makeText(context, message, Toast.LENGTH_LONG)
                    }.onFailure {
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG)
                    }
                }
            }
        }
    }

    suspend fun loadRelease() {
        _state.emit(ReleasePageViewModelState.Loading)

        _state.emit(
            runCatching {
                val id = releaseId
                    ?: error("release id is null")

                val release = getRelease(id).getOrNull()
                    ?: error("failed to receive the release")

                colorExtractor.fetchImageByUrl(release.coverArtUrl
                    ?: error("release art is null")
                )

                val tracks = getReleaseTracks(id).getOrNull()
                    ?: error("failed to receive the release tracks")

                val otherReleases = release.artists.flatMap { artist ->
                    getArtistReleasesUseCase(artist.id)
                        .getOrDefault(listOf())
                }.filter { release ->
                    release.id != id
                }

                ReleasePageViewModelState.Success(
                    tracks, release, otherReleases
                ).also { it }
            }.fold(
                { it },
                { ReleasePageViewModelState.Error(it) }
            )
        )
    }
}