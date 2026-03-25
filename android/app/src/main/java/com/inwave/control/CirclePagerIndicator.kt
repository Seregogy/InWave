package com.inwave.control

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.inwave.tool.indicatorOffsetForPage

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun CirclePagerIndicator(
    modifier: Modifier = Modifier,
    count: Int,
    state: PagerState,
    circleColor: Color = Color.White,
    minSize: Dp = 5.dp,
    maxSize: Dp = 12.dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            for (i in 0 until count) {
                val offset = state.indicatorOffsetForPage(i)

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(circleColor)
                        .height(minSize)
                        .width(lerp(minSize, maxSize, offset))
                )
            }
        }
    }
}