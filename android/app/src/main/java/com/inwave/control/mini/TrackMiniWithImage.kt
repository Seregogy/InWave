package com.inwave.control.mini

import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.inwave.R
import com.inwave.control.MarqueeText
import com.inwave.domain.entity.Track

@Composable
fun TrackMiniWithImage(
    modifier: Modifier = Modifier,
    track: Track,
    onPrimaryColor: Color = Color.White,
    onClick: (it: Track) -> Unit = { }
) {
    val context = LocalContext.current

    val isCurrentlyPlay by remember {
        derivedStateOf {

        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(track)
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = modifier
                .padding(end = 30.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(track.coverArtUrl)
                        .build(),
                    error = painterResource(R.drawable.image_item_placeholder)
                ),
                contentDescription = "",
                modifier = Modifier
                    .height(45.dp)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )

            Column {
                MarqueeText(
                    text = track.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W700,
                    maxLines = 1,
                    color = onPrimaryColor,
                    modifier = Modifier
                        .basicMarquee()
                )

                MarqueeText(
                    text = track.artists.map { it.artist }.joinToString(",") { it.name },
                    maxLines = 1,
                    color = onPrimaryColor,
                    modifier = Modifier
                        .basicMarquee(),
                    fontSize = 14.sp
                )
            }
        }

        IconButton(
            modifier = Modifier
                .align(Alignment.CenterEnd),
            onClick = {
                //TODO: контекстный bottom sheet
            }
        ) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = "dots",
                tint = onPrimaryColor
            )
        }
    }
}

/*
@Composable
fun TrackMiniWithImage(
    modifier: Modifier = Modifier,
    track: BaseTrackWithArtists = BaseTrackWithArtists(),
    primaryColor: Color,
    onPrimaryColor: Color = Color.White,
    onClick: (it: BaseTrack) -> Unit = { }
) {
    val isCurrentlyPlay by remember {
        derivedStateOf {
            AudioPlayer.currentlyPlayTrackId.value == track.id
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick(track.run {
                    BaseTrack(
                        id = id,
                        name = name,
                        imageUrl = imageUrl,
                        indexInAlbum = indexInAlbum
                    )
                })
            }
            .then(
                if (isCurrentlyPlay)
                    Modifier.background(primaryColor.copy(.1f))
                else
                    Modifier
            )
            .padding(start = 20.dp, end = 10.dp)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = track.imageUrl,
                contentDescription = "mini track image",
                modifier = Modifier
                    .height(50.dp)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )

            Column {
                Text(
                    text = track.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W700,
                    maxLines = 1,
                    color = onPrimaryColor,
                    modifier = Modifier
                        .basicMarquee()
                )

                Text(
                    text = track.artists.joinToString(",") { it.name },
                    maxLines = 1,
                    color = onPrimaryColor,
                    modifier = Modifier
                        .basicMarquee(),
                    fontSize = 14.sp
                )
            }
        }

        IconButton(
            modifier = Modifier
                .align(Alignment.CenterEnd),
            onClick = {
                //TODO: контекстный bottom sheet
            }
        ) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = "dots"
            )
        }
    }

}*/
