package com.inwave.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inwave.player.state.PlayerStateSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TimerState {
    class Idle : TimerState()

    class Active(
        val amountSeconds: Long,
        val remainingTimeFlow: Flow<Long>
    ) : TimerState()

    class Finished : TimerState()
    class Cancelled : TimerState()
}

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val playerStateSource: PlayerStateSource
) : ViewModel() {
    private val _state: MutableStateFlow<TimerState> = MutableStateFlow(TimerState.Idle())
    val state: StateFlow<TimerState> = _state

    private var timerJob: Job? = null

    fun startTimer(amountSeconds: Long) {
        timerJob = viewModelScope.launch {
            if (_state.value is TimerState.Active)
                cancelTimer()

            val remainingTimeFlow = MutableStateFlow(amountSeconds).also {
                _state.value = TimerState.Active(amountSeconds, it)
            }

            var remaining = amountSeconds
            while (remaining > 0 && isActive) {
                delay(1000)
                remaining--
                remainingTimeFlow.value = remaining
            }

            if (remaining == 0L && isActive) {
                playerStateSource.pause()
                _state.value = TimerState.Finished()
            }
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        _state.value = TimerState.Cancelled()
    }
}