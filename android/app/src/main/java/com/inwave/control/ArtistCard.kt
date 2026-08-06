package com.inwave.control

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inwave.control.scaffold.color.ColoredScaffold
import com.inwave.control.scaffold.color.rememberColoredScaffoldState
import com.inwave.domain.entity.Artist
import com.inwave.tool.ImagePaletteExtractor

@Composable
fun ArtistCard(
    modifier: Modifier = Modifier,
    artist: Artist,
    imagePaletteExtractor: ImagePaletteExtractor,
    onClick: (artist: Artist) -> Unit
) {
    val density = LocalDensity.current
    val bitmap by imagePaletteExtractor.bitmap.collectAsState()

    var cardHeight by remember { mutableStateOf(0.dp) }
    var contentHeight by remember { mutableStateOf(0.dp) }

    LaunchedEffect(artist) {
        artist.imagesUrl.first().let {
            imagePaletteExtractor.fetchImageByUrl(it)
        }
    }

    ColoredScaffold(
        state = rememberColoredScaffoldState {
            imagePaletteExtractor.palette.collectAsState()
        }
    ) {
        Box(
            modifier = modifier
                .onSizeChanged {
                    cardHeight = with(density) {
                        it.height.toDp()
                    }
                }
                .clip(RoundedCornerShape(16.dp))
                .clickable { onClick(artist) }
                .background(Color.Black)
                .background(primaryOrBackgroundColor.value.copy(.2f))
        ) {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                        .drawWithContent {
                            drawContent()

                            drawRect(
                                brush = Brush.verticalGradient(
                                    0f to Color.White,
                                    (cardHeight - contentHeight - 80.dp) / cardHeight to Color.White,
                                    (cardHeight - contentHeight + 50.dp) / cardHeight to Color.Transparent,
                                    1f to Color.Transparent
                                ),
                                blendMode = BlendMode.DstIn
                            )
                        },
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .onSizeChanged {
                        contentHeight = with(density) {
                            it.height.toDp()
                        }
                    }
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = artist.name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.W700,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )

                artist.about?.let {
                    Text(
                        text = it,
                        fontWeight = FontWeight.W600,
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White.copy(.7f)
                    )
                }
            }
        }
    }
}