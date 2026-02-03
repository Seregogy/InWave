package com.inwave.control.scaffold.color

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.palette.graphics.Palette

class ColoredScaffoldState(
    var currentPalette: State<Palette?> = mutableStateOf(null),
    val animationSpec: AnimationSpec<Color> = tween(durationMillis = 700, easing = LinearEasing),

    var backgroundColor: State<Color> = mutableStateOf(Color.Black),
    var onBackgroundColor: State<Color> = mutableStateOf(Color.White),

    var primaryOrBackgroundColor: State<Color> = mutableStateOf(Color.Yellow),
    var onPrimaryOrBackgroundColor: State<Color> = mutableStateOf(Color.Black),
    var bodyTextOnBackground: State<Color> = mutableStateOf(Color.White),

    var backgroundColorAnimated: State<Color> = mutableStateOf(Color.Black),
    var onBackgroundColorAnimated: State<Color> = mutableStateOf(Color.White),
    var bodyTextOnBackgroundAnimated: State<Color> = mutableStateOf(Color.White),

    var primaryOrBackgroundColorAnimated: State<Color> = mutableStateOf(Color.Yellow),
    var onPrimaryOrBackgroundColorAnimated: State<Color> = mutableStateOf(Color.Black),

    var textOnPrimaryOrBackgroundColor: State<Color> = mutableStateOf(Color.White),
    var textOnPrimaryOrBackgroundColorAnimated: State<Color> = mutableStateOf(Color.White),

    var additionalVerticalGradientBrush: MutableState<Brush> = mutableStateOf(SolidColor(Color.Transparent)),
    var additionalHorizontalGradientBrush: MutableState<Brush> = mutableStateOf(SolidColor(Color.Transparent))
)

@Composable
fun rememberColoredScaffoldState(): ColoredScaffoldState {
    return remember {
        mutableStateOf(ColoredScaffoldState())
    }.value
}

@Composable
fun rememberColoredScaffoldState(currentPalette: @Composable () -> State<Palette?>): ColoredScaffoldState {
    val palette = currentPalette()

    return remember {
        mutableStateOf(ColoredScaffoldState(palette))
    }.value
}

@Composable
fun rememberColoredScaffoldState(
    animationSpec: AnimationSpec<Color>,
    currentPalette: @Composable () -> State<Palette?>
): ColoredScaffoldState {
    val palette = currentPalette()

    return remember {
        mutableStateOf(
            ColoredScaffoldState(
                currentPalette = palette,
                animationSpec = animationSpec
            )
        )
    }.value
}