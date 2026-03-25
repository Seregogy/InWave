package com.inwave.page.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Preview(showBackground = true)
@Composable
fun ArtistPageSkeleton() {
    Box(
        modifier = Modifier
            .fillMaxHeight(.7f)
            .fillMaxWidth()
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()

                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(.2f),
                            Color.Transparent,
                        )
                    ),
                    blendMode = BlendMode.DstIn
                )
            }
            .background(Color.Gray)
    )

    Column(
        modifier = Modifier
            .shimmer()
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.55f)
                .padding(bottom = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(20.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(Color.Gray)
                )

                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(35.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(Color.Gray)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .width(65.dp)
                                .height(30.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(Color.Gray)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Column(Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .padding(start = 25.dp)
                    .width(120.dp)
                    .height(25.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(Color.Gray)
            )

            Spacer(Modifier.height(15.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .padding(start = 20.dp)
                        .padding(vertical = 5.dp)
                        .size(80.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(Color.Gray)
                )

                Spacer(Modifier.width(15.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(20.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(Color.Gray)
                    )

                    Box(
                        modifier = Modifier
                            .width(65.dp)
                            .height(20.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(Color.Gray)
                    )
                }
            }

            Spacer(Modifier.height(15.dp))

            Box(
                modifier = Modifier
                    .padding(start = 25.dp)
                    .width(120.dp)
                    .height(25.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(Color.Gray)
            )

            Spacer(Modifier.height(15.dp))

            repeat(4) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .padding(start = 20.dp)
                            .padding(vertical = 5.dp)
                            .size(55.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(Color.Gray)
                    )

                    Spacer(Modifier.width(7.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(70.dp)
                                .height(15.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(Color.Gray)
                        )

                        Box(
                            modifier = Modifier
                                .width(65.dp)
                                .height(15.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(Color.Gray)
                        )
                    }
                }
            }
        }
    }
}