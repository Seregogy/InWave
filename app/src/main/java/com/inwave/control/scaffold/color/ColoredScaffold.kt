package com.inwave.control.scaffold.color

import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.inwave.tool.contrast

@Composable
fun ColoredScaffold(
    state: ColoredScaffoldState,
    content: @Composable (ColoredScaffoldState.() -> Unit)
) {
    state.run {
        CalculateColors()
        CalculateColorAnimations()
        CalculateAdditionalGradient()
    }

    with(state) {
        content()
    }
}

@Composable
private fun ColoredScaffoldState.CalculateColors() {
    val colorScheme = MaterialTheme.colorScheme

    primaryOrBackgroundColor = remember {
        derivedStateOf {
            return@derivedStateOf if (currentPalette.value?.vibrantSwatch == null) {
                Color(currentPalette.value?.dominantSwatch?.rgb
                    ?: colorScheme.tertiary.toArgb()
                )
            } else {
                Color(currentPalette.value?.vibrantSwatch?.rgb!!)
            }
        }
    }

    onPrimaryOrBackgroundColor = remember {
        derivedStateOf {
            return@derivedStateOf if (currentPalette.value?.vibrantSwatch == null) {
                Color(
                    currentPalette.value?.dominantSwatch?.titleTextColor
                        ?: colorScheme.tertiary.toArgb()
                )
            } else {
                Color(currentPalette.value?.vibrantSwatch?.titleTextColor!!).copy(0.8f)
            }
        }
    }

    textOnPrimaryOrBackgroundColor = remember {
        derivedStateOf {
            return@derivedStateOf if (currentPalette.value?.vibrantSwatch != null) {
                if (currentPalette.value?.vibrantSwatch?.rgb!!.contrast(currentPalette.value?.dominantSwatch?.rgb) < 3f) {
                    Color(
                        currentPalette.value?.dominantSwatch?.titleTextColor
                            ?: colorScheme.onTertiary.toArgb()
                    )
                } else {
                    Color(
                        currentPalette.value?.vibrantSwatch?.rgb
                            ?: colorScheme.tertiary.toArgb()
                    )
                }
            } else {
                Color(
                    currentPalette.value?.dominantSwatch?.titleTextColor
                        ?: colorScheme.onTertiary.toArgb()
                ).copy(0.8f)
            }
        }
    }

    backgroundColor = remember {
        derivedStateOf {
            Color(currentPalette.value?.dominantSwatch?.rgb ?: colorScheme.background.toArgb())
        }
    }

    onBackgroundColor = remember {
        derivedStateOf {
            Color(
                currentPalette.value?.dominantSwatch?.titleTextColor
                    ?: colorScheme.onBackground.toArgb()
            )
        }
    }

    bodyTextOnBackground = remember {
        derivedStateOf {
            Color(
                currentPalette.value?.dominantSwatch?.bodyTextColor
                    ?: colorScheme.onBackground.toArgb()
            )
        }
    }
}

@Composable
private fun ColoredScaffoldState.CalculateColorAnimations() {
    backgroundColorAnimated = animateColorAsState(
        targetValue = backgroundColor.value,
        label = "animated background value",
        animationSpec = animationSpec
    )

    onBackgroundColorAnimated = animateColorAsState(
        targetValue = onBackgroundColor.value,
        label = "animated background value",
        animationSpec = animationSpec
    )

    bodyTextOnBackgroundAnimated = animateColorAsState(
        targetValue = bodyTextOnBackground.value,
        label = "animated body text on background",
        animationSpec = animationSpec
    )

    primaryOrBackgroundColorAnimated = animateColorAsState(
        targetValue = primaryOrBackgroundColor.value,
        label = "animated background value",
        animationSpec = animationSpec
    )

    onPrimaryOrBackgroundColorAnimated = animateColorAsState(
        targetValue = onPrimaryOrBackgroundColor.value,
        label = "animated background value",
        animationSpec = animationSpec
    )

    textOnPrimaryOrBackgroundColorAnimated = animateColorAsState(
        targetValue = textOnPrimaryOrBackgroundColor.value,
        label = "animated background value",
        animationSpec = animationSpec
    )
}

@Composable
private fun ColoredScaffoldState.CalculateAdditionalGradient() {
    currentPalette.value?.swatches?.takeLast(3)?.map {
        animateColorAsState(
            targetValue = Color(it.rgb).copy(.2f),
            label = "gradient_color_anim",
            animationSpec = animationSpec
        ).value
    }?.let {
        additionalVerticalGradientBrush.value = remember(it) {
            Brush.verticalGradient(
                colors = it
            )
        }
        additionalHorizontalGradientBrush.value = remember(it) {
            Brush.horizontalGradient(
                colors = it.take(2)
            )
        }
    }
}