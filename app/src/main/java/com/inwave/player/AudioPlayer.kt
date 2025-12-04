package com.inwave.player

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.inwave.domain.entity.Lyrics
import com.inwave.domain.entity.Track
import com.inwave.domain.repository.TrackRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.collections.listOf

val defaultTrack = Track(
    id = "unknown",
    name = "unknown",
    imageUrl = "",
    indexInAlbum = -1,
    duration = 0,
    null,
    0,
    "",
    null
)

data class PlayerTrack(
    val data: Track,
    val mediaItem: MediaItem
)

class AudioPlayer(
    private val mediaController: MediaController,
    private val repository: TrackRepository
) {
    companion object {
        private val _currentlyPlayTrackId: MutableState<String?> = mutableStateOf(null)
        val currentlyPlayTrackId: State<String?> = _currentlyPlayTrackId

        private val _currentPlaylistId: MutableState<String?> = mutableStateOf(null)
        private val currentPlaylistId: State<String?> = _currentPlaylistId
    }

    private val _playlist = MutableStateFlow<MutableList<PlayerTrack>>(mutableListOf())
    val playlist: StateFlow<List<PlayerTrack>> = _playlist

    private val _isLastTrack = mutableStateOf(false)
    val isLastTrack: State<Boolean> = _isLastTrack

    private val _currentPlayerTrack = MutableStateFlow<PlayerTrack?>(null)
    val currentPlayerTrack: StateFlow<PlayerTrack?> = _currentPlayerTrack

    private val _currentLyrics = MutableStateFlow<Lyrics?>(null)
    val currentLyrics: StateFlow<Lyrics?> = _currentLyrics

    private val _currentTrackDuration = MutableStateFlow(1L)
    val currentTrackDuration: StateFlow<Long> = _currentTrackDuration

    private val _currentState: MutableStateFlow<AudioPlayerState> = MutableStateFlow(AudioPlayerState.Idle)
    val currentState: StateFlow<AudioPlayerState> = _currentState

    private val _repeatModeState: MutableStateFlow<RepeatMode> = MutableStateFlow(RepeatMode.Playlist)
    val repeatModeState: StateFlow<RepeatMode> = _repeatModeState

    val currentPosition: MutableState<Long> = mutableLongStateOf(0L)

    // TODO: переработать плейлисты
    //private var _currentPlaylistProvider: PlaylistProvider? = null

    private val listener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)

            _currentPlayerTrack.value = _playlist.value.first { it.mediaItem == mediaItem }.also { track ->
                _currentlyPlayTrackId.value = track.data.id
                _isLastTrack.value = _playlist.value.indexOf(track) == _playlist.value.size - 1

                _currentLyrics.value = track.data.lyrics

                /*_currentPlaylistProvider?.let { playlistProvider ->
                    if (_playlist.value.size - _playlist.value.indexOf(track) - 1 < 3) {
                        CoroutineScope(Dispatchers.Main).launch {
                            addToPlaylist(playlistProvider.getAdditionalTracks(5, 1))
                        }
                    }
                }*/
            }
        }

        override fun onPlaybackStateChanged(state: Int) {
            super.onPlaybackStateChanged(state)

            when (state) {
                Player.STATE_READY -> {
                    _currentState.value = AudioPlayerState.Ready
                    _currentTrackDuration.value = mediaController.contentDuration
                }
                Player.STATE_ENDED -> {
                    _currentState.value = AudioPlayerState.Ended
                }
                Player.STATE_BUFFERING -> {
                    _currentState.value = AudioPlayerState.Loading
                }
                Player.STATE_IDLE -> {
                    _currentState.value = AudioPlayerState.Idle
                }
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)

            when(repeatMode) {
                MediaController.REPEAT_MODE_ONE -> _repeatModeState.value = RepeatMode.Single
                MediaController.REPEAT_MODE_ALL -> _repeatModeState.value = RepeatMode.Playlist
                MediaController.REPEAT_MODE_OFF -> _repeatModeState.value = RepeatMode.None
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            Log.e("Player", "${error.message}\ncause: ${error.cause}\ncode:${error.errorCode}")
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                _currentState.value = AudioPlayerState.Play
            } else {
                _currentState.value = AudioPlayerState.Pause
            }
        }
    }

    init {
        mediaController.addListener(listener)
        mediaController.repeatMode = MediaController.REPEAT_MODE_ALL

        CoroutineScope(Dispatchers.Main).launch {
            do {
                currentPosition.value = mediaController.currentPosition

                if (mediaController.duration != 0L && _currentTrackDuration.value == mediaController.contentDuration) {
                    _currentTrackDuration.value = mediaController.contentDuration
                }

                delay(500)
            } while (_currentState.value != AudioPlayerState.Released)
        }
    }

    fun playPause() {
        if (mediaController.isPlaying)
            pause()
        else
            play()
    }

    fun seek(positionMs: Long) {
        mediaController.seekTo(positionMs)
    }

    fun seekToNext() {
        mediaController.seekToNext()
    }

    fun seekToPrev() {
        mediaController.seekToPrevious()
    }

    fun seekToIndex(index: Int): Boolean {
        if ((index in _playlist.value.indices).not()) return false

        mediaController.seekToDefaultPosition(index)
        return true
    }

    fun nextRepeatMode() {
        when (_repeatModeState.value) {
            RepeatMode.Single -> mediaController.repeatMode = MediaController.REPEAT_MODE_ALL
            RepeatMode.Playlist -> mediaController.repeatMode = MediaController.REPEAT_MODE_OFF
            RepeatMode.None -> mediaController.repeatMode = MediaController.REPEAT_MODE_ONE
        }
    }

    fun play() {
        mediaController.play()
    }

    fun pause() {
        mediaController.pause()
    }

    suspend fun addToPlaylist(tracks: List<String>, playlistId: String = "", playAfterLoad: Boolean = false) {
        val cachedTracks = preparePlaylistTracks(tracks)

        _playlist.value.addAll(cachedTracks)

        mediaController.apply {
            addMediaItems(cachedTracks.map { it.mediaItem })
            prepare()

            if (playAfterLoad) {
                play()
            }
        }
    }

//    suspend fun setPlaylist(playlistProvider: PlaylistProvider) {
//        _currentPlaylistProvider = playlistProvider
//        setPlaylist(playlistProvider.getTracks())
//    }

    @Deprecated(
        message = "Этот API может привести к некорректной работе плейлистов",
        replaceWith = ReplaceWith("setPlaylist(playlistProvider: PlaylistProvider)", imports = ["com.example.ktor_test_client.player.AudioPlayer"])
    )
    suspend fun setPlaylist(tracks: List<String>, startTrackIndex: Int = 0) {
        val cachedTracks = preparePlaylistTracks(tracks)

        _playlist.value.clear()
        _playlist.value.addAll(cachedTracks)

        Log.d("Playlist", tracks.toString())

        _playlist.value.forEach {
            Log.d("Playlist", it.toString())
        }
        mediaController.apply {
            stop()
            setMediaItems(cachedTracks.map { it.mediaItem })
            prepare()
            seekToDefaultPosition(startTrackIndex)
            play()


        }
    }

    private suspend fun preparePlaylistTracks(tracks: List<String>, playlistId: String = ""): List<PlayerTrack> {
        _currentPlaylistId.value = playlistId

        val fetchedTracks = repository.getTracks(tracks).fold(
            onSuccess = { it },
            onFailure = {
                throw it
                Log.e("Playlist", it.stackTrace.toString())
                listOf()
            }
        )

        val playerTracks = fetchedTracks.map { track ->
            PlayerTrack(
                data = track,
                mediaItem = MediaItem.Builder()
                    .setMediaId(track.id)
                    .setUri(track.audioUrl)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(track.name)
                            .setDisplayTitle(track.name)
                            .setAlbumTitle(track.album?.name ?: "unknown")
                            .setArtist(track.album?.artists?.joinToString(",") { it.name } ?: "unknown")
                            .setArtworkUri(Uri.parse(track.imageUrl))
                            .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                            .build()
                    )
                    .build()
            )
        }

        return playerTracks
    }

    suspend fun fetchCurrentTrackWithLyrics() {
        currentPlayerTrack.value?.let { playerTrack ->
            _currentPlayerTrack.value?.copy(
                data = repository.getTrackWithLyrics(playerTrack.data.id).getOrNull() ?: defaultTrack,
                mediaItem = playerTrack.mediaItem
            )
        }
    }

    suspend fun lazyClearPlaylist() {
        _currentState.first { it != AudioPlayerState.Play }

        mediaController.stop()
        mediaController.clearMediaItems()
        _playlist.value.clear()
    }

    fun release() {
        mediaController.stop()
        mediaController.release()
        mediaController.removeListener(listener)

        _currentState.value = AudioPlayerState.Released
    }

    enum class AudioPlayerState {
        Idle,
        Ready,
        Play,
        Pause,
        Ended,
        Loading,
        Released
    }

    enum class RepeatMode {
        Single,
        Playlist,
        None
    }
}