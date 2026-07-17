package com.inwave.control.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anhaki.picktime.PickHourMinuteSecond
import com.anhaki.picktime.utils.PickTimeFocusIndicator
import com.anhaki.picktime.utils.PickTimeTextStyle
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
        isDraggable = false,
        label = stringResource(R.string.timer),
        description = stringResource(R.string.setup_timer)
    ) { padding ->
        val state by viewModel.state.collectAsStateWithLifecycle()
        val hapticFeedback = LocalHapticFeedback.current

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
                        var hour by remember { mutableIntStateOf(0) }
                        var minute by remember { mutableIntStateOf(0) }
                        var second by remember { mutableIntStateOf(0) }

                        PickHourMinuteSecond(
                            initialHour = hour,
                            onHourChange = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                hour = it
                            },
                            initialMinute = minute,
                            onMinuteChange = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                minute = it
                            },
                            initialSecond = second,
                            onSecondChange = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                second = it
                            },
                            selectedTextStyle = PickTimeTextStyle(
                                color = Color.White,
                                fontSize = 26.sp,
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold,
                            ),
                            unselectedTextStyle = PickTimeTextStyle(
                                color = Color.White.copy(.6f),
                                fontSize = 20.sp,
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Normal,
                            ),
                            verticalSpace = 15.dp,
                            horizontalSpace = 15.dp,
                            containerColor = Color.Black,
                            isLooping = true,
                            extraRow = 2,
                            focusIndicator = PickTimeFocusIndicator(
                                enabled = true,
                                widthFull = true,
                                background = Color.White.copy(.15f),
                                shape = RectangleShape,
                                border = BorderStroke(0.dp, Color(0xFF87CDE6)),
                            )
                        )


                        TextButton(
                            onClick = {
                                viewModel.startTimer(
                                    hour.toLong() * 3600 + minute * 60 + second
                                )
                            },
                            enabled = hour + minute + second != 0
                        ) {
                            Text(
                                text = stringResource(R.string.start_timer),
                                color = Color(0xFFC8E6C9)
                            )
                        }

                    }
                    is TimerState.Active -> {
                        val remainsTime by currentState.remainingTimeFlow.collectAsStateWithLifecycle(0L)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 25.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val hours = (remainsTime / 3600)
                                .toString()
                                .padStart(2, '0')

                            val minutes = (remainsTime % 3600 / 60)
                                .toString()
                                .padStart(2, '0')

                            val seconds = (remainsTime % 60)
                                .toString()
                                .padStart(2, '0')

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SlidablyNumberedCounter(hours)
                                Text(":", fontSize = 50.sp, color = Color.White.copy(.5f))
                                SlidablyNumberedCounter(minutes)
                                Text(":", fontSize = 50.sp, color = Color.White.copy(.5f))
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
