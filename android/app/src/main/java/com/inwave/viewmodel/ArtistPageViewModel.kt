package com.inwave.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Release
import com.inwave.domain.entity.Track
import com.inwave.domain.usecase.artist.query.GetArtistReleasesUseCase
import com.inwave.domain.usecase.artist.query.GetArtistTopTracksUseCase
import com.inwave.domain.usecase.artist.query.GetArtistUseCase
import com.inwave.tool.ImagePaletteExtractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed class ArtistPageViewModelState() {
    object Idle: ArtistPageViewModelState()
    object Loading: ArtistPageViewModelState()
    class Success(
        val artist: Artist,
        val latestRelease: Release,
        val topTracks: List<Track>,
        val albums: List<Release>,
        val singles: List<Release>
    ): ArtistPageViewModelState()
    class Error(val exception: Throwable): ArtistPageViewModelState()
}

@HiltViewModel
class ArtistPageViewModel @Inject constructor(
    private val colorExtractor: ImagePaletteExtractor,
    private val getArtist: GetArtistUseCase,
    private val getTopTracks: GetArtistTopTracksUseCase,
    private val getArtistReleases: GetArtistReleasesUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val artistId: String? = savedStateHandle["artistId"]

    val palette: StateFlow<Palette?> = colorExtractor.palette
    val bitmap: StateFlow<Bitmap?> = colorExtractor.bitmap

    private val _state = MutableStateFlow<ArtistPageViewModelState>(ArtistPageViewModelState.Idle)
    val state: StateFlow<ArtistPageViewModelState> = _state

    suspend fun loadArtist() {

    }
}