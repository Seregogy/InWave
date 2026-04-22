package com.inwave.control

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.inwave.control.mini.TrackMiniWithImage
import com.inwave.domain.entity.Artist
import com.inwave.domain.entity.Track

@Composable
fun ArtistWithTracks(
    modifier: Modifier,
    artist: Artist,
    tracks: List<Track>,
    onTrackClick: (trackId: String) -> Unit
) {
    Box(modifier) {
        Column(Modifier.padding(10.dp)) {
            Text(
                text = artist.name,
                fontWeight = FontWeight.W700,
                fontSize = 26.sp
            )

            Spacer(Modifier.height(10.dp))

            tracks.forEach {
                TrackMiniWithImage(
                    modifier = Modifier.padding(vertical = 3.dp),
                    track = it,
                    onPrimaryColor = Color.White,
                    onClick = { onTrackClick(it.id) }
                )
            }
        }
    }
}