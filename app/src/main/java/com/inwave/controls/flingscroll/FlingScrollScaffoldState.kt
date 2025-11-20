package com.inwave.controls.flingscroll

import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class FlingScrollScaffoldState(
    val onScrollStateChange: FlingScrollScaffoldState.() -> Unit = { },
    val yFlingOffset: Dp = 0.dp
) {
    var screenHeight: Dp = 0.dp
    var screenWidth: Dp = 0.dp

    lateinit var density: Density
    lateinit var lazyListState: LazyListState
    lateinit var noSnapLayout: SnapLayoutInfoProvider
    lateinit var snapLayoutInfoProvider: SnapLayoutInfoProvider

    var isHeaderSwiped: MutableState<Boolean> = mutableStateOf(true)

    var alpha: MutableFloatState = mutableFloatStateOf(0f)
    var colorAlpha: MutableFloatState = mutableFloatStateOf(0f)
    var isHeaderVisible: MutableState<Boolean> = mutableStateOf(false)
    var currentOffset: MutableState<Dp> = mutableStateOf(0.dp)
    var currentItemIndex: MutableState<Int> = mutableStateOf(0)
    var totalHeight: MutableState<Dp> = mutableStateOf(0.dp)
}

@Composable
fun rememberFlingScaffoldState(): FlingScrollScaffoldState {
    return remember {
        mutableStateOf(FlingScrollScaffoldState())
    }.value
}

@Composable
fun rememberFlingScaffoldState(
    yFlingOffset: Dp = 0.dp,
    onScrollStateChange: FlingScrollScaffoldState.() -> Unit
): FlingScrollScaffoldState {
    val isHeaderSwiped = rememberSaveable {
        mutableStateOf(true)
    }

    return remember {
        mutableStateOf(
            FlingScrollScaffoldState(onScrollStateChange, yFlingOffset).apply {
                this.isHeaderSwiped = isHeaderSwiped
            }
        )
    }.value
}