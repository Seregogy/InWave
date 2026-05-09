package com.inwave.control.mini

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inwave.control.MarqueeText
import com.inwave.domain.entity.Track
import com.inwave.tool.times

@Composable
fun TrackMini(
    modifier: Modifier = Modifier,
    track: Track,
    primaryColor: Color,
    onPrimaryColor: Color = Color.White,
    indexPlusOne: Boolean = true,
    infiniteTransition: InfiniteTransition = rememberInfiniteTransition("infinity transition animation"),
    onClick: (it: Track) -> Unit = { },
    onContextAction: (it: Track) -> Unit = { }
) {
    val haptic = LocalHapticFeedback.current

    val isCurrentlyPlay by remember {
        derivedStateOf {
            //AudioPlayer.currentlyPlayTrackId.value == track.id
            false
        }
    }

    val animatedValue by infiniteTransition.animateFloat(
        initialValue = .7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "animated_float_value"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    onClick(track)
                },
                onLongClick = {
                    onContextAction(track)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )
            .then(
                if (isCurrentlyPlay)
                    Modifier.background(primaryColor.copy(.3f))
                else
                    Modifier
            )
            .padding(start = 20.dp, end = 10.dp)
            .padding(vertical = 5.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (isCurrentlyPlay) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .scale(animatedValue)
                        .clip(CircleShape)
                        .size(12.dp)
                        .background(primaryColor * 1.5f)
                )
            }
        } else {
            Text(
                text = (track.placeInRelease ?: 0).plus(
                    if(indexPlusOne) 1 else 0
                ).toString(),
                color = onPrimaryColor * .8f,
                fontWeight = FontWeight.W700,
                fontSize = 12.sp
            )
        }

        MarqueeText(
            text = track.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.W600,
            maxLines = 1,
            color = onPrimaryColor,
            modifier = Modifier
                .padding(horizontal = 25.dp),
            textAlign = Alignment.CenterStart
        )

        IconButton(
            modifier = Modifier
                .align(Alignment.CenterEnd),
            onClick = {
                onContextAction(track)
            }
        ) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = "dots"
            )
        }
    }
}

