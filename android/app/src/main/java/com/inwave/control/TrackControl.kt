package com.inwave.control

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.inwave.domain.entity.Track

@Composable
fun TrackControl(
    modifier: Modifier = Modifier,
    track: Track,
    onClick: (it: Track) -> Unit = { },
    controls: @Composable RowScope.() -> Unit
) {
    val artistsNames = track.artists.map { it.artist }.joinToString(", ") { it.name }

    Row(
        modifier = modifier
            .clickable {
                onClick(track)
            }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .weight(5f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            AsyncImage(
                model = track.coverArtUrl,
                modifier = Modifier
                    .height(50.dp)
                    .aspectRatio(1f)
                    .fillMaxHeight()
                    .clip(MaterialTheme.shapes.small),
                contentDescription = "mini track image",
                contentScale = ContentScale.Crop
            )

            Column {
                MarqueeText(
                    text = track.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W700,
                    color = Color.White,
                    maxLines = 1,
                    lineHeight = 18.sp,
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