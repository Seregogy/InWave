package com.inwave.control

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.inwave.domain.entity.Track
import com.inwave.player.state.PLAYER_POSITION_PULLING_DELAY_MS

@Composable
fun TrackControl(
    modifier: Modifier = Modifier,
    track: Track,

    fillBrush: Brush = SolidColor(Color.White.copy(.1f)),
    trackTimelinePosition: Float = 0f,
    onClick: (it: Track) -> Unit = { },
    onDoubleClick: () -> Unit = { },
    controls: @Composable RowScope.() -> Unit
) {
    val density = LocalDensity.current
    val artistsNames = track.artists.joinToString(", ") { it.artist.name }
    var trackControlsHeight by remember { mutableStateOf(DpSize.Zero) }

    val trackTimelinePositionAnimated by animateFloatAsState(
        targetValue = trackTimelinePosition,
        animationSpec = tween(
            durationMillis = PLAYER_POSITION_PULLING_DELAY_MS.toInt(),
            easing = LinearEasing
        )
    )

    Box(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF171717))
            .combinedClickable(
                onClick = { onClick(track) },
                onDoubleClick = onDoubleClick
            )
    ) {
        Box(
            modifier = Modifier
                .height(trackControlsHeight.height)
                .background(fillBrush)
                .fillMaxWidth(trackTimelinePositionAnimated)
                .align(Alignment.CenterStart)
        )

        Row(
            modifier = Modifier
                .onSizeChanged {
                    with(density) {
                        trackControlsHeight = DpSize(it.width.toDp(), it.height.toDp())
                    }
                }
                .padding(7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(5f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AsyncImage(
                    model = track.coverArtUrl,
                    modifier = Modifier
                        .height(42.dp)
                        .aspectRatio(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(5.dp)),
                    contentDescription = "mini track image",
                    contentScale = ContentScale.Crop
                )

                Column {
                    MarqueeText(
                        text = track.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W700,
                        color = Color.White,
                        maxLines = 1,
                        lineHeight = 16.sp,
                        textAlign = Alignment.CenterStart
                    )

                    MarqueeText(
                        text = artistsNames,
                        fontSize = 13.sp,
                        color = Color.White.copy(.7f),
                        lineHeight = 13.sp,
                        textAlign = Alignment.CenterStart
                    )
                }
            }

            controls()
        }
    }
}