package com.inwave.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track
import com.inwave.domain.usecase.artist.query.GetArtistReleasesUseCase
import com.inwave.domain.usecase.release.query.GetReleaseTracksUseCase
import com.inwave.domain.usecase.release.query.GetReleaseUseCase
import com.inwave.tool.ImagePaletteExtractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val colorExtractor: ImagePaletteExtractor,
    private val getRelease: GetReleaseUseCase,
    private val getReleaseTracks: GetReleaseTracksUseCase,
    private val getArtistReleasesUseCase: GetArtistReleasesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val releaseId: String? = savedStateHandle["releaseId"]

    val palette: StateFlow<Palette?> = colorExtractor.palette
    val bitmap: StateFlow<Bitmap?> = colorExtractor.bitmap

    private val _state = MutableStateFlow<ReleasePageViewModelState>(ReleasePageViewModelState.Idle)
    val state: StateFlow<ReleasePageViewModelState> = _state

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
                )
            }.fold(
                { it },
                { ReleasePageViewModelState.Error(it) }
            )
        )
    }
}