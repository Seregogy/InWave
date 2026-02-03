package com.inwave.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AvatarRow(
    modifier: Modifier = Modifier,
    spaceBetween: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    val spaceBetweenPx = with(LocalDensity.current) {
        spaceBetween.roundToPx()
    }

    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        val totalHeight = placeables.firstOrNull()?.height?.coerceIn(0..16777215) ?: 0
        val totalWidth = ((placeables.firstOrNull()?.height ?: 0)
                + placeables.drop(1).sumOf { it.width / 2 }
                + (spaceBetweenPx * (placeables.size - 1)))
            .coerceIn(0..16777215)

        layout(
            width = totalWidth,
            height = totalHeight
        ) {
            var xPosition = 0

            placeables.forEach { placeable ->
                placeable.placeRelative(
                    x = xPosition,
                    y = 0
                )
                xPosition += placeable.width / 2 + spaceBetweenPx
            }
        }
    }
}