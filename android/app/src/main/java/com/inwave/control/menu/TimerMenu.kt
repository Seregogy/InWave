package com.inwave.control.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inwave.R
import com.inwave.control.SlidablyNumberedCounter
import com.inwave.viewmodel.TimerState
import com.inwave.viewmodel.TimerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerMenu(
    viewModel: TimerViewModel,
    expanded: MutableState<Boolean>,
    containerColor: Color = Color.Black,
    onTimerSet: (amountSec: Long) -> Unit = { },
    onTimerCancel: () -> Unit = { }
) {
    ContextMenu(
        expanded = expanded,
        containerColor = containerColor,
        label = stringResource(R.string.timer),
        description = stringResource(R.string.setup_timer)
    ) { padding ->
        val state by viewModel.state.collectAsStateWithLifecycle()

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(vertical = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                when (val currentState = state) {
                    is TimerState.Finished, is TimerState.Idle, is TimerState.Cancelled -> {
                        val timeInputState = rememberTimePickerState(
                            initialHour = 0,
                            initialMinute = 10,
                            is24Hour = true
                        )

                        TimeInput(timeInputState)

                        TextButton(
                            onClick = {
                                viewModel.startTimer(
                                    timeInputState.hour.toLong() * 3600 + timeInputState.minute * 60
                                )
                            }
                        ) {
                            Text(stringResource(R.string.start_timer))
                        }

                    }
                    is TimerState.Active -> {
                        val remainsTime by currentState.remainingTimeFlow.collectAsStateWithLifecycle(0L)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 25.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val minutes = (remainsTime / 60)
                                .toString()
                                .padStart(2, '0')

                            val seconds = (remainsTime % 60)
                                .toString()
                                .padStart(2, '0')

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SlidablyNumberedCounter(minutes)
                                Text(":", fontSize = 32.sp)
                                SlidablyNumberedCounter(seconds)
                            }
                        }

                        TextButton(
                            onClick = {
                                viewModel.cancelTimer()
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.cancel_timer),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}
