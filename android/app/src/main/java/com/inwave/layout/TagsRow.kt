package com.inwave.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun TagsRow(
    modifier: Modifier = Modifier,
    verticalSpace: Dp = 0.dp,
    horizontalSpace: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val screenWidthPx = with(density) {
        screenWidth.roundToPx()
    }

    val verticalSpacePx = with(density) {
        verticalSpace.roundToPx()
    }

    val horizontalSpacePx = with(density) {
        horizontalSpace.roundToPx()
    }

    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        var totalHeight = 0

        var widthSum = 0
        var maxPlaceableHeightInRow = 0

        val positions = mutableListOf<Pair<Int, Int>>()
        for (i in 0..<placeables.size) {
            positions.add(widthSum to totalHeight)

            widthSum += placeables[i].width + horizontalSpacePx

            maxPlaceableHeightInRow = max(maxPlaceableHeightInRow, placeables[i].height)

            if (widthSum + placeables[(i + 1).coerceIn(0..<placeables.size)].width > screenWidthPx) {
                totalHeight += maxPlaceableHeightInRow + verticalSpacePx

                widthSum = 0
                maxPlaceableHeightInRow = 0
            }
        }

        layout(constraints.maxWidth, totalHeight + maxPlaceableHeightInRow) {
            placeables.zip(positions).forEach {
                it.first.place(
                    x = it.second.first,
                    y = it.second.second
                )
            }
        }
    }
}