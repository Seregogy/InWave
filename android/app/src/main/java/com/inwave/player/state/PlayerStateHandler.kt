package com.inwave.player.state

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.invawe.data.mapper.track.toMediaItem
import com.inwave.domain.entity.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlayerStateHandler(
    val playerStateSource: PlayerStateSource,
    val mediaController: MediaController
): Player.Listener {
    private var isProcessingExternalUpdate = false
    private val handlerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    init {
        mediaController.addListener(this)
        observeCommands()

        observeRepeatMode()
        handlePosition()

        restoreState()
    }

    private fun handlePosition() {
        handlerScope.launch {
            do {
                playerStateSource.currentPosition.value = mediaController.currentPosition

                if (mediaController.duration != 0L && mediaController.duration == mediaController.contentDuration) {
                    playerStateSource.currentTrackDuration.value = mediaController.contentDuration
                }

                delay(500)
            } while (playerStateSource.currentState.value != PlayerState.Released())
        }
    }

    private fun observeCommands() {
        handlerScope.launch {
            playerStateSource.currentCommand.collect { command ->
                if (isProcessingExternalUpdate) return@collect

                Log.d("PlayerStateHandler", "Processing: ${command.javaClass.simpleName}")
                executeCommand(command)
            }
        }
    }

    private fun executeCommand(command: PlayerCommand) {
        try {
            when (command) {
                is PlayerCommand.Play -> {
                    if (mediaController.playbackState == Player.STATE_IDLE) {
                        mediaController.prepare()
                    }
                    mediaController.play()
                }
                is PlayerCommand.Pause -> mediaController.pause()
                is PlayerCommand.Stop -> mediaController.stop()
                is PlayerCommand.Seek -> {
                    mediaController.seekTo(command.currentPosition)
                    playerStateSource.currentPosition.value = mediaController.currentPosition
                }
                is PlayerCommand.Next -> mediaController.seekToNext()
                is PlayerCommand.Prev -> mediaController.seekToPrevious()
                is PlayerCommand.SeekToIndex -> {
                    mediaController.seekToDefaultPosition(command.index)
                }
                is PlayerCommand.SeekToMediaItem -> {
                    mediaController.setMediaItem(command.mediaItem)
                    mediaController.prepare()
                }
                is PlayerCommand.SetTracks -> {
                    setTracks(command.tracks)
                    Log.d("PlayerStateHandler", command.tracks.toString())
                }
                is PlayerCommand.AddTracks -> {
                    addTracks(command.tracks)
                }
                is PlayerCommand.SetRepeatMode -> {
                    mediaController.repeatMode = when(command.repeatMode) {
                        PlayerState.RepeatMode.Single -> MediaController.REPEAT_MODE_ONE
                        PlayerState.RepeatMode.Playlist -> MediaController.REPEAT_MODE_ALL
                        PlayerState.RepeatMode.Forward -> MediaController.REPEAT_MODE_OFF
                    }
                }
                is PlayerCommand.Release -> {
                    release()
                }
            }
        } catch (e: Exception) {
            Log.e("PlayerStateHandler", "Error executing $command", e)
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)

        mediaItem?.let { item ->
            updateCurrentTrack(item)
            Log.d("Player", item.mediaMetadata.title.toString())
            checkIfLastTrack()
        }
    }

    private fun updateCurrentTrack(mediaItem: MediaItem) {
        playerStateSource.playlist.value.firstOrNull { it.id == mediaItem.mediaId }?.let {
            playerStateSource.currentTrack.value = it
        }
    }

    private fun checkIfLastTrack() {
        val playlist = playerStateSource.playlist.value
        val currentTrack = playerStateSource.currentTrack.value

        if (playlist.isNotEmpty() && currentTrack != null) {
            val currentIndex = playlist.indexOfFirst { it.id == currentTrack.id }
            playerStateSource.isLastTrack.value = currentIndex == playlist.size - 1
        }
    }

    override fun onPlaybackStateChanged(state: Int) {
        isProcessingExternalUpdate = true

        playerStateSource.currentState.value = when (state) {
            Player.STATE_READY -> PlayerState.Ready()
            Player.STATE_IDLE -> PlayerState.Idle()
            Player.STATE_BUFFERING -> PlayerState.Loading()
            Player.STATE_ENDED -> PlayerState.Ended()
            else -> PlayerState.Idle()
        }

        playerStateSource.isPlaying.value = mediaController.isPlaying

        playerStateSource.currentTrackDuration.value = mediaController.duration

        isProcessingExternalUpdate = false
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        super.onRepeatModeChanged(repeatMode)

        playerStateSource.currentRepeatModeState.value = when(repeatMode) {
            MediaController.REPEAT_MODE_ONE -> PlayerState.RepeatMode.Single
            MediaController.REPEAT_MODE_ALL -> PlayerState.RepeatMode.Playlist
            MediaController.REPEAT_MODE_OFF -> PlayerState.RepeatMode.Forward
            else -> PlayerState.RepeatMode.Playlist
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        Log.e("PlayerStateHandler", "${error.message}\ncause: ${error.cause}\ncode:${error.errorCode}")
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (isProcessingExternalUpdate) return

        playerStateSource.isPlaying.value = isPlaying

        if (isPlaying && playerStateSource.currentState.value is PlayerState.Ready) {
            playerStateSource.currentState.value = PlayerState.Play()
        } else if (!isPlaying && playerStateSource.currentState.value is PlayerState.Play) {
            playerStateSource.currentState.value = PlayerState.Pause()
        }
    }

    private fun observeRepeatMode() {
        handlerScope.launch {
            playerStateSource.currentRepeatModeState.collect { repeatMode ->
                val exoRepeatMode = when (repeatMode) {
                    PlayerState.RepeatMode.Single -> Player.REPEAT_MODE_ONE
                    PlayerState.RepeatMode.Playlist -> Player.REPEAT_MODE_ALL
                    PlayerState.RepeatMode.Forward -> Player.REPEAT_MODE_OFF
                }
                mediaController.repeatMode = exoRepeatMode
            }
        }
    }

    fun setTracks(tracks: List<Track>, startIndex: Int = 0) {
        val mediaItems = tracks.map { it.toMediaItem() }
        mediaController.setMediaItems(mediaItems)

        prepareTracks(mediaItems, startIndex)
    }

    fun addTracks(tracks: List<Track>, startIndex: Int = 0) {
        val mediaItems = tracks.map { it.toMediaItem() }
        mediaController.addMediaItems(mediaItems)

        prepareTracks(mediaItems, startIndex)
    }

    private fun prepareTracks(mediaItems: List<MediaItem>, startIndex: Int) {
        if (startIndex > 0) {
            mediaController.seekToDefaultPosition(startIndex)
        }

        if (mediaItems.isNotEmpty()) {
            mediaController.prepare()
        }
    }

    private fun restoreState() {
        if (playerStateSource.playlist.value.isNotEmpty()) {
            setTracks(playerStateSource.playlist.value)

            val position = playerStateSource.currentPosition.value
            if (position > 0) {
                mediaController.seekTo(position)
            }

            if (playerStateSource.isPlaying.value) {
                handlerScope.launch {
                    playerStateSource.play()
                }
            }
        }
    }

    fun release() {
        handlerScope.cancel()

        Log.d("PlayerStateHandler", "release player")

        mediaController.stop()
        mediaController.release()
        mediaController.removeListener(this)
        playerStateSource.currentState.value = PlayerState.Released()
    }
}