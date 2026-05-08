package com.inwave.player.state

import android.util.Log
import androidx.media3.common.MediaItem
import com.inwave.domain.entity.Track
import com.inwave.domain.usecase.track.query.GetRandomTrackIdUseCase
import com.inwave.domain.usecase.track.query.GetTrackUseCase
import com.inwave.domain.usecase.track.query.GetTrackWithLyricsUseCase
import com.inwave.domain.usecase.track.query.GetTracksUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
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
    private val getRandomTrackId: GetRandomTrackIdUseCase,
    private val getTrackWithLyrics: GetTrackWithLyricsUseCase,
    scope: CoroutineScope
) {
    private val _playlist = MutableStateFlow<MutableList<Track>>(mutableListOf())
    val playlist: StateFlow<List<Track>> = _playlist

    val currentCommand: MutableSharedFlow<PlayerCommand> = MutableSharedFlow(100)

    val currentState: MutableStateFlow<PlayerState> = MutableStateFlow(PlayerState.Idle())
    val currentTrack: MutableStateFlow<Track?> = MutableStateFlow(null)
    val isLastTrack: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val currentTrackDuration: MutableStateFlow<Long> = MutableStateFlow(1L)
    val currentRepeatModeState: MutableStateFlow<PlayerState.RepeatMode> = MutableStateFlow(PlayerState.RepeatMode.Forward)
    val currentPosition: MutableStateFlow<Long> = MutableStateFlow(1L)
    val isPlaying: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        flow {
            while (true) {
                delay(5000)
                emit(Unit)
            }
        }
        .filter { currentRepeatModeState.value == PlayerState.RepeatMode.Forward }
        .filter {
            val remainTracksInPlaylist = playlist.value.size - playlist.value.indexOf(currentTrack.value)
            remainTracksInPlaylist < 5
        }
        .onStart { loadRandomTracks(5) }
        .onEach { loadRandomTracks(5) }
        .launchIn(scope)
    }

    private suspend fun loadRandomTracks(amount: Int) {
        //TODO: сделать отдельный ендпоинт для получения рандомных id списком
        repeat(amount) {
            getRandomTrackId().onSuccess {
                addToPlaylist(listOf(it))
            }
        }
    }

    suspend fun playPause() {
        if (isPlaying.value) {
            currentCommand.emit(PlayerCommand.Pause())
        } else {
            currentCommand.emit(PlayerCommand.Play())
        }
    }

    suspend fun seek(positionMs: Long) {
        currentCommand.emit(PlayerCommand.Seek(positionMs))
    }

    suspend fun seekToNext() {
        currentCommand.emit(PlayerCommand.Next())
    }

    suspend fun seekToPrev() {
        currentCommand.emit(PlayerCommand.Prev())
    }

    suspend fun seekToIndex(index: Int) {
        currentCommand.emit(PlayerCommand.SeekToIndex(index))
    }

    suspend fun play() {
        currentCommand.emit(PlayerCommand.Play())
        Log.d("PlayerStateSource", "play command sent")
    }

    suspend fun pause() {
        currentCommand.emit(PlayerCommand.Pause())
    }

    suspend fun release() {
        currentCommand.emit(PlayerCommand.Release())
    }

    suspend fun nextRepeatMode() {
        val repeatMode = when (currentRepeatModeState.value) {
            PlayerState.RepeatMode.Single -> PlayerState.RepeatMode.Playlist
            PlayerState.RepeatMode.Playlist -> PlayerState.RepeatMode.Forward
            PlayerState.RepeatMode.Forward -> PlayerState.RepeatMode.Single
        }

        currentCommand.emit(PlayerCommand.SetRepeatMode(repeatMode))
    }

    suspend fun addToPlaylist(tracks: List<String>) {
        val newTracks = preparePlaylistTracks(tracks)

        _playlist.value.addAll(newTracks)
        _playlist.value = _playlist.value.plus(newTracks).toMutableList()

        currentCommand.emit(PlayerCommand.AddTracks(newTracks))
    }

    suspend fun setPlaylist(tracks: List<Track>) {
        _playlist.value.clear()
        _playlist.value.addAll(tracks)

        Log.d("PlayerStateSource", tracks.toString())

        currentCommand.emit(PlayerCommand.SetTracks(tracks))
    }

    suspend fun setPlaylistById(ids: List<String>) {
        val newTracks = preparePlaylistTracks(ids)

        _playlist.value.clear()
        _playlist.value.addAll(newTracks)

        currentCommand.emit(PlayerCommand.SetTracks(newTracks))
        Log.d("PlayerStateSource", ids.toString())
    }

    private suspend fun preparePlaylistTracks(tracks: List<String>): List<Track> {
        return getTracks(tracks).fold(
            onSuccess = { it },
            onFailure = { listOf() }
        )
    }

    suspend fun fetchCurrentTrackWithLyrics() {
        Log.d("PlayerStateSource", "start lrc fetch")
        currentTrack.value?.let { track ->
            getTrackWithLyrics(track.id).onSuccess { trackWithLyrics ->
                Log.d("PlayerStateSource", "Lyrics fetched")
                currentTrack.value = trackWithLyrics
                Log.d("PlayerStateSource", currentTrack.value.toString())

            }
        }
    }

    suspend fun lazyClearPlaylist() {
        currentCommand.first { it != PlayerCommand.Play() }
        _playlist.value.clear()
    }
}