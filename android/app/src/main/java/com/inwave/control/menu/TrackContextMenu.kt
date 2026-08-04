package com.inwave.control.menu

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Headset
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inwave.domain.entity.Track
import com.inwave.layout.TagsRow
import com.inwave.tool.formatMinuteTimer
import com.inwave.tool.formatNumber
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun TrackAdditionalDataContextMenu(
    track: Track,
    expanded: MutableState<Boolean>,
    padding: PaddingValues,
    imagePrimaryColor: Color,
    onLikeClick: () -> Unit = { },
    onAddToPlaylistClick: () -> Unit = { },
    onDownloadClick: () -> Unit = { },
    onLyricsClick: () -> Unit = { },
    onReleaseClick: () -> Unit = { },
    onArtistClick: () -> Unit = { },
    onShareClick: () -> Unit = { }
) {
    ContextMenu(expanded) { padding ->
        TrackAdditionalDataMenu(
            track = track,
            padding = padding,
            imagePrimaryColor = imagePrimaryColor,
            onLikeClick = onLikeClick,
            onAddToPlaylistClick = onAddToPlaylistClick,
            onDownloadClick = onDownloadClick,
            onLyricsClick = onLyricsClick,
            onReleaseClick = onReleaseClick,
            onArtistClick = onArtistClick,
            onShareClick = onShareClick
        )
    }
}

@Composable
fun TrackAdditionalDataMenu(
    track: Track,
    padding: PaddingValues,
    imagePrimaryColor: Color,
    onLikeClick: () -> Unit = { },
    onAddToPlaylistClick: () -> Unit = { },
    onDownloadClick: () -> Unit = { },
    onLyricsClick: () -> Unit = { },
    onReleaseClick: () -> Unit = { },
    onArtistClick: () -> Unit = { },
    onShareClick: () -> Unit = { }
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    var descriptionExpanded by remember { mutableStateOf(false) }

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight - 100.dp)
                .background(
                    Brush.radialGradient(
                        listOf(imagePrimaryColor.copy(.3f), Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = 1500f
                    )
                )
        )

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp)
                .heightIn(max = screenHeight - 100.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            item {
                Column {
                    Text(
                        text = track.additionalData?.fullTitle ?: track.name,
                        modifier = Modifier
                            .padding(top = 15.dp),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.W700,
                        lineHeight = 28.sp
                    )

                    Text(
                        text = "${track.release?.name}",
                        fontWeight = FontWeight.W500,
                        color = Color.White.copy(.7f)
                    )

                    Text(
                        text = "${track.release?.artists?.joinToString(", ") { it.name }}",
                        fontWeight = FontWeight.W500,
                        color = Color.White.copy(.7f)
                    )
                }
            }

            item {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Headset,
                            contentDescription = "",
                            tint = Color.White.copy(.7f),
                            modifier = Modifier.size(16.dp)
                        )

                        Text(
                            text = (track.statistics?.playCount?.formatNumber() ?: "N/A") +  " • " +
                                    formatMinuteTimer((track.durationMs?.toInt() ?: 0) / 1000),
                            fontWeight = FontWeight.W500,
                            color = Color.White.copy(.7f)
                        )
                    }



                    //TODO: добавить в доменную модель дату релиза
                    /*Text(
                        text = track.additionalData?.toDate() ?: "",
                        fontWeight = FontWeight.W500,
                        color = Color.White.copy(.7f)
                    )*/
                }
            }

            item {
                ContextButtons(
                    onLikeClick = onLikeClick,
                    onAddToPlaylistClick = onAddToPlaylistClick,
                    onDownloadClick = onDownloadClick,
                    onLyricsClick = onLyricsClick,
                    onReleaseClick = onReleaseClick,
                    onArtistClick = onArtistClick
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .animateContentSize()
                        .clip(MaterialTheme.shapes.small)
                        .clickable {
                            descriptionExpanded = !descriptionExpanded
                        }
                        .background(Color.White.copy(.07f))
                        .padding(15.dp)
                ) {
                    MarkdownText(
                        markdown = if(!descriptionExpanded)
                                (track.additionalData?.descriptionPreviewPlainText + "...")
                            else
                                track.additionalData?.descriptionMarkdown ?: "ass",
                        syntaxHighlightColor = Color.White.copy(.07f),
                        style = TextStyle(
                            color = Color.White.copy(.7f),
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize
                        )
                    )
                }
            }

            item {
                track.additionalData?.tags?.let { tags ->
                    TagsRow(
                        horizontalSpace = 8.dp,
                        verticalSpace = 8.dp
                    ) {
                        tags.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.White.copy(.07f))
                                    .clickable {
                                        //TODO: Поиск по тегу при нажатии
                                    }
                                    .padding(horizontal = 10.dp)
                                    .padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = "#${tag}",
                                    fontWeight = FontWeight.W500,
                                    fontSize = 12.sp,
                                    color = Color.White.copy(.7f)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(25.dp))
            }
        }
    }
}

@Composable
fun ContextButtons(
    modifier: Modifier = Modifier,
    onLikeClick: () -> Unit = { },
    onAddToPlaylistClick: () -> Unit = { },
    onDownloadClick: () -> Unit = { },
    onLyricsClick: () -> Unit = { },
    onReleaseClick: () -> Unit = { },
    onArtistClick: () -> Unit = { },
    onShareClick: () -> Unit = { }
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(Color.White.copy(.07f))
    ) {
        ContextMenuButton(Icons.Rounded.Favorite, "Нравится", { })
        HorizontalDivider(Modifier
            .align(Alignment.CenterHorizontally)
            .fillMaxWidth(.9f)
            .alpha(.3f))

        ContextMenuButton(Icons.Rounded.BookmarkAdd, "Добавить в плейлист", { })
        HorizontalDivider(Modifier
            .align(Alignment.CenterHorizontally)
            .fillMaxWidth(.9f)
            .alpha(.3f))

        ContextMenuButton(Icons.Rounded.Download, "Скачать", { })
        HorizontalDivider(Modifier
            .align(Alignment.CenterHorizontally)
            .fillMaxWidth(.9f)
            .alpha(.3f))

        ContextMenuButton(Icons.Rounded.Lyrics, "Текст", { })
        HorizontalDivider(Modifier
            .align(Alignment.CenterHorizontally)
            .fillMaxWidth(.9f)
            .alpha(.3f))

        ContextMenuButton(Icons.Rounded.Album, "Перейти к релизу", { })
        HorizontalDivider(Modifier
            .align(Alignment.CenterHorizontally)
            .fillMaxWidth(.9f)
            .alpha(.3f))

        ContextMenuButton(Icons.Rounded.Person, "Перейти к артисту", { })
        HorizontalDivider(Modifier
            .align(Alignment.CenterHorizontally)
            .fillMaxWidth(.9f)
            .alpha(.3f))

        ContextMenuButton(Icons.Rounded.Share, "Поделиться", { })
    }
}

@Composable
private fun ContextMenuButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(vertical = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(.7f),
            modifier = Modifier
                .size(16.dp)
        )

        Text(
            text = text,
            color = Color.White.copy(.7f),
            fontWeight = FontWeight.W500,
            fontSize = 16.sp
        )
    }
}