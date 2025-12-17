package com.inwave.player.state

import android.util.Log
import androidx.media3.common.MediaItem
import com.inwave.domain.entity.Track
import com.inwave.domain.usecase.track.GetTrackUseCase
import com.inwave.domain.usecase.track.GetTrackWithLyricsUseCase
import com.inwave.domain.usecase.track.GetTracksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Singleton

sealed class PlayerCommand {
    class Play: PlayerCommand()
    class Pause: PlayerCommand()
    class Seek(val currentPosition: Long): PlayerCommand()
    class SeekToIndex(val index: Int): PlayerCommand()
    class SeekToMediaItem(val mediaItem: MediaItem): PlayerCommand()
    class Next: PlayerCommand()
    class Prev: PlayerCommand()
    class SetTracks(val tracks: List<Track>): PlayerCommand()
    class AddTracks(val tracks: List<Track>): PlayerCommand()
    class SetRepeatMode(val repeatMode: PlayerState.RepeatMode): PlayerCommand()
    class Stop: PlayerCommand()
    class Release: PlayerCommand()
}

sealed class PlayerState {
    class Idle: PlayerState()
    class Ready: PlayerState()
    class Play: PlayerState()
    class Pause: PlayerState()
    class Ended: PlayerState()
    class Loading: PlayerState()
    class Released: PlayerState()

    enum class RepeatMode {
        Single,
        Playlist,
        Forward
    }
}

@Singleton
class PlayerStateSource(
    private val getTrack: GetTrackUseCase,
    private val getTracks: GetTracksUseCase,
    private val getTrackWithLyrics: GetTrackWithLyricsUseCase
) {
    private val _playlist = MutableStateFlow<MutableList<Track>>(mutableListOf())
    val playlist: StateFlow<List<Track>> = _playlist

    private val _currentCommand: MutableStateFlow<PlayerCommand> = MutableStateFlow(PlayerCommand.Stop())
    val currentCommand: StateFlow<PlayerCommand> = _currentCommand

    val currentState: MutableStateFlow<PlayerState> = MutableStateFlow(PlayerState.Idle())
    val currentTrack: MutableStateFlow<Track?> = MutableStateFlow(null)
    val isLastTrack: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val currentTrackDuration: MutableStateFlow<Long> = MutableStateFlow(1L)
    val currentRepeatModeState: MutableStateFlow<PlayerState.RepeatMode> = MutableStateFlow(PlayerState.RepeatMode.Playlist)
    val currentPosition: MutableStateFlow<Long> = MutableStateFlow(1L)
    val isPlaying: MutableStateFlow<Boolean> = MutableStateFlow(false)

    fun playPause() {
        if (isPlaying.value) {
            _currentCommand.value = PlayerCommand.Pause()
        } else {
            _currentCommand.value = PlayerCommand.Play()
        }
    }

    suspend fun seek(positionMs: Long) {
        _currentCommand.emit(PlayerCommand.Seek(positionMs))
    }

    suspend fun seekToNext() {
        _currentCommand.emit(PlayerCommand.Next())
    }

    suspend fun seekToPrev() {
        _currentCommand.emit(PlayerCommand.Prev())
    }

    suspend fun seekToIndex(index: Int) {
        _currentCommand.emit(PlayerCommand.SeekToIndex(index))
    }

    suspend fun play() {
        _currentCommand.emit(PlayerCommand.Play())
    }

    suspend fun pause() {
        _currentCommand.emit(PlayerCommand.Pause())
    }

    suspend fun release() {
        _currentCommand.emit(PlayerCommand.Release())
    }

    suspend fun nextRepeatMode() {
        val repeatMode = when (currentRepeatModeState.value) {
            PlayerState.RepeatMode.Single -> PlayerState.RepeatMode.Playlist
            PlayerState.RepeatMode.Playlist -> PlayerState.RepeatMode.Forward
            PlayerState.RepeatMode.Forward -> PlayerState.RepeatMode.Single
        }

        _currentCommand.emit(PlayerCommand.SetRepeatMode(repeatMode))
    }

    suspend fun addToPlaylist(tracks: List<String>) {
        val newTracks = preparePlaylistTracks(tracks)

        _playlist.value.addAll(newTracks)
        _playlist.value = _playlist.value.plus(newTracks).toMutableList()

        _currentCommand.emit(PlayerCommand.AddTracks(newTracks))
    }

    suspend fun setPlaylist(tracks: List<Track>) {
        _playlist.value.clear()
        _playlist.value.addAll(tracks)

        _currentCommand.emit(PlayerCommand.SetTracks(tracks))
    }

    suspend fun setPlaylistById(ids: List<String>) {
        val newTracks = preparePlaylistTracks(ids)

        _playlist.value.clear()
        _playlist.value.addAll(newTracks)

        _currentCommand.value = PlayerCommand.SetTracks(newTracks)
        Log.d("PlayerStateSource", ids.toString())
    }

    private suspend fun preparePlaylistTracks(tracks: List<String>): List<Track> {
        return getTracks(tracks).fold(
            onSuccess = { it },
            onFailure = { listOf() }
        )
    }

    suspend fun fetchCurrentTrackWithLyrics() {
        currentTrack.value?.let { track ->
            getTrackWithLyrics(track.id).onSuccess { trackWithLyrics ->
                currentTrack.value = trackWithLyrics
            }
        }
    }

    suspend fun lazyClearPlaylist() {
        _currentCommand.first { it != PlayerCommand.Play() }
        _playlist.value.clear()
    }
}