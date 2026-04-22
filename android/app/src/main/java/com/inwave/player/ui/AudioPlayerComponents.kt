package com.inwave.player.ui

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.inwave.R
import com.inwave.control.CircleButton
import com.inwave.control.ContextMenu
import com.inwave.control.MarqueeText
import com.inwave.control.scaffold.color.ColoredScaffoldState
import com.inwave.domain.entity.Track
import com.inwave.layout.AvatarRow
import com.inwave.player.state.PlayerState
import com.inwave.tool.formatMinuteTimer
import com.inwave.tool.times
import com.inwave.viewmodel.AudioPlayerViewModel
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import androidx.compose.runtime.collectAsState

@Composable
fun ColoredScaffoldState.TopBar(
    modifier: Modifier = Modifier,
    track: Track?,
    onCollapseRequest: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                onCollapseRequest()
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_down_icon),
                contentDescription = "",
                modifier = Modifier
                    .size(30.dp),
                tint = onBackgroundColorAnimated.value
            )
        }

        MarqueeText(
            text = "Плейлист \"${track?.name ?: "unknown"}\"",
            fontWeight = FontWeight.W700,
            color = onBackgroundColorAnimated.value,
            maxLines = 1,
            textAlign = Alignment.Center,
            containerModifier = Modifier
                .weight(.6f)
        )


        IconButton(
            onClick = { }
        ) {
            Icon(
                painter = painterResource(R.drawable.queue_music_icon),
                contentDescription = "",
                tint = onBackgroundColorAnimated.value
            )
        }
    }
}

@Composable
fun ColoredScaffoldState.MainContent(
    viewModel: AudioPlayerViewModel = hiltViewModel(),
    isLyricsOpen: MutableState<Boolean>,
    screenWidth: Dp,
) {
    val currentImage by viewModel.imagePaletteExtractor.bitmap.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .height(screenWidth)
            .offset(y = (-20).dp)
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()

                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White,
                            Color.Transparent,
                        )
                    ),
                    blendMode = BlendMode.DstIn
                )
            }
    ) {
        AnimatedVisibility(
            visible = isLyricsOpen.value,
            enter = fadeIn(tween()),
            exit = fadeOut(tween())
        ) {
            LyricsDrawer(viewModel)
        }

        AnimatedVisibility(
            visible = !isLyricsOpen.value,
            enter = fadeIn(tween()),
            exit = fadeOut(tween())
        ) {
            currentImage?.let {
                AnimatedContent(
                    targetState = it,
                    transitionSpec = {
                        fadeIn(tween(animationsSpeed)) togetherWith fadeOut(
                            tween(
                                animationsSpeed
                            )
                        )
                    },
                    label = "image animation"
                ) { animatedBitmap ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    ) {
                        Image(
                            bitmap = animatedBitmap.asImageBitmap(),
                            contentDescription = "release image",
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ColoredScaffoldState.TrackInfo(
    track: Track?,
    isTrackLoading: State<Boolean>,
    onReleaseClick: (releaseId: String) -> Unit,
    onArtistClick: (artistId: String) -> Unit
) {
    val artistsSheet = remember { mutableStateOf(false) }
    val density = LocalDensity.current

    val infiniteTransition = rememberInfiniteTransition("cycling animation transition")
    val textAlphaAnimated by infiniteTransition.animateFloat(
        initialValue = .3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "text alpha animation"
    )

    val textAlpha by remember {
        derivedStateOf {
            if (isTrackLoading.value) textAlphaAnimated else 1f
        }
    }

    var columnSize by remember { mutableStateOf(IntSize.Zero) }

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (columnSize.height != 0) {
            AvatarRow(
                spaceBetween = 5.dp
            ) {
                track?.artists?.forEach { artistOnTrack ->
                    Box {
                        AsyncImage(
                            model = artistOnTrack.artist.imagesUrl,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .height(with(density) { columnSize.height.toDp() })
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .clickable {
                                    if (artistOnTrack.artist.id.isNotBlank())
                                        onArtistClick(artistOnTrack.artist.id)
                                },
                            contentDescription = "mini avatar"
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .onSizeChanged {
                    columnSize = it
                }
        ) {
            MarqueeText(
                text = track?.name ?: "",
                fontSize = 30.sp,
                fontWeight = FontWeight.W800,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable {
                        track?.releaseId?.let {
                            onReleaseClick(it)
                        }
                    }
                    .alpha(textAlpha),
                color = textOnPrimaryOrBackgroundColorAnimated.value
            )

            MarqueeText(
                text = track?.artists?.map { it.artist }?.joinToString(", ") { it.name } ?: "unknown",
                fontWeight = FontWeight.W600,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable {
                        if ((track?.artists?.size ?: 0) == 1) {
                            track?.artists?.first()?.artist?.id?.let {
                                onArtistClick(it)
                            }
                        } else {
                            artistsSheet.value = true
                        }
                    }
                    .alpha(textAlpha),
                color = onBackgroundColorAnimated.value
            )
        }
    }

    ContextMenu(artistsSheet) { padding ->
        track?.artists?.let { artistsOnTrack ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 25.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                item {
                    Text(
                        text = "Артисты",
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .padding(bottom = 15.dp),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.W700
                    )
                }

                items(artistsOnTrack) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            .clickable {
                                onArtistClick(it.artist.id)
                                artistsSheet.value = false
                            }
                            .padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        AsyncImage(
                            model = it.artist.imagesUrl,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(60.dp),
                            contentDescription = ""
                        )

                        Text(
                            text = it.artist.name,
                            fontWeight = FontWeight.W600
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColoredScaffoldState.PlayerSlider(
    modifier: Modifier = Modifier,
    currentTrackDuration: Long,
    viewModel: AudioPlayerViewModel,
    isSliding: MutableState<Boolean>,
) {
    val coroutine = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    var localCurrentPos by remember { mutableFloatStateOf(0f) }

    val tickCount = 50
    val sliderGap = 1f / tickCount
    var currentGap by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        viewModel.currentPosition.collect {
            if (isSliding.value.not()) {
                localCurrentPos = it / viewModel.trackDuration.value.toFloat()
            }
        }
    }

    val semiTransparentForeground by remember {
        derivedStateOf {
            onBackgroundColorAnimated.value.copy(.65f)
        }
    }

    val fullyTransparentForeground by remember {
        derivedStateOf {
            onBackgroundColorAnimated.value.copy(.15f)
        }
    }

    val currentPositionAnimated = animateFloatAsState(
        targetValue = localCurrentPos,
        animationSpec = if (isSliding.value) tween(0) else tween(500, easing = LinearEasing),
        label = "slider animation"
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Slider(
            modifier = modifier
                .weight(1f),
            value = currentPositionAnimated.value,
            onValueChange = {
                if ((localCurrentPos - currentGap).absoluteValue > sliderGap) {
                    haptic.performHapticFeedback(
                        HapticFeedbackType.SegmentFrequentTick
                    )
                    currentGap = localCurrentPos
                }

                if (!isSliding.value) isSliding.value = true
                localCurrentPos = it
            },
            onValueChangeFinished = {
                isSliding.value = false

                viewModel.seek((localCurrentPos * currentTrackDuration.toFloat()).toLong())
            },
            colors = SliderDefaults.colors(
                activeTrackColor = semiTransparentForeground * 1.5f,
                activeTickColor = semiTransparentForeground * 2f,
                inactiveTrackColor = fullyTransparentForeground,
                inactiveTickColor = semiTransparentForeground,
                disabledThumbColor = semiTransparentForeground,
                disabledActiveTrackColor = semiTransparentForeground,
                disabledActiveTickColor = semiTransparentForeground,
                disabledInactiveTickColor = semiTransparentForeground,
                disabledInactiveTrackColor = semiTransparentForeground,
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(30.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(onBackgroundColorAnimated.value)
                )
            }
        )

        IconButton(
            onClick = {
                haptic.performHapticFeedback(
                    HapticFeedbackType.Confirm
                )

                coroutine.launch {
                    viewModel.toggleLike()
                }
            }
        ) {
            if (viewModel.isCurrentTrackLiked.collectAsState().value) {
                Log.d("PlayerComponent", "Play particles")
            }

            Icon(
                imageVector = if (viewModel.isCurrentTrackLiked.collectAsState().value)
                    Icons.Rounded.Favorite
                else
                    Icons.Rounded.FavoriteBorder,
                contentDescription = "play/pause icon",
                tint = onBackgroundColorAnimated.value,
                modifier = Modifier
                    .size(26.dp)
            )
        }
    }
}

enum class TimingTextState {
    CurrentTime,
    RemainingTime
}

@Composable
fun ColoredScaffoldState.TimingText(
    secondaryColorWithLoadingState: Color,
    currentPosition: State<Long>,
    currentTrackDuration: Long,
    isSliding: MutableState<Boolean>
) {
    val haptic = LocalHapticFeedback.current

    val currentPositionAnimated = animateFloatAsState(
        targetValue = (currentPosition.value / currentTrackDuration.toFloat()),//.coerceIn(0f..currentTrackDuration.toFloat()),
        animationSpec = if (isSliding.value) tween(0) else tween(300, easing = LinearEasing),
        label = "slider animation"
    )

    val fullyTransparentForeground by remember {
        derivedStateOf {
            onBackgroundColorAnimated.value.copy(.15f)
        }
    }

    var currentTextState by remember { mutableStateOf(TimingTextState.CurrentTime) }

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(fullyTransparentForeground)
            .clickable {
                currentTextState = if (currentTextState == TimingTextState.CurrentTime) {
                    haptic.performHapticFeedback(
                        HapticFeedbackType.ContextClick
                    )

                    TimingTextState.RemainingTime
                } else {
                    haptic.performHapticFeedback(
                        HapticFeedbackType.ContextClick
                    )

                    TimingTextState.CurrentTime
                }
            }
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontSize = 13.sp
                    )
                ) {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.W800
                        )
                    ) {
                        append(
                            formatMinuteTimer(
                                if (currentTextState == TimingTextState.CurrentTime) {
                                    (currentPositionAnimated.value * currentTrackDuration.toFloat() / 1000)
                                } else {
                                    -(currentTrackDuration - currentPositionAnimated.value * currentTrackDuration.toFloat()) / 1000
                                }.roundToInt().coerceIn(-currentTrackDuration.toInt()..currentTrackDuration.toInt())
                            )
                        )
                    }

                    append(" / ")

                    append(formatMinuteTimer((currentTrackDuration / 1000).toInt()))
                }
            },
            textAlign = TextAlign.Center,
            color = secondaryColorWithLoadingState
        )
    }
}

@Composable
fun ColoredScaffoldState.PlayerNavigationButtons(
    modifier: Modifier = Modifier,
    secondaryColorWithLoadingState: Color,
    isPlay: State<Boolean>,
    isLastTrack: State<Boolean>,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onPlayPause: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val nextTrackLoadedColorState by remember {
        derivedStateOf {
            if (isLastTrack.value) {
                onBackgroundColorAnimated.value.copy(.3f)
            } else {
                onBackgroundColorAnimated.value
            }
        }
    }

    val fullyTransparentForeground by remember {
        derivedStateOf {
            onBackgroundColorAnimated.value.copy(.15f)
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        IconButton(
            onClick = {
                haptic.performHapticFeedback(
                    HapticFeedbackType.ContextClick
                )
                onPrev()
            }
        ) {
            Icon(
                imageVector = Icons.Rounded.SkipPrevious,
                contentDescription = "prev icon",
                tint = secondaryColorWithLoadingState,
                modifier = Modifier
                    .size(34.dp)
            )
        }

        CircleButton(
            containerColor = fullyTransparentForeground,
            size = 70.dp,
            onClick = {
                haptic.performHapticFeedback(
                    HapticFeedbackType.Confirm
                )
                onPlayPause()
            },
            content = {
                Icon(
                    imageVector = if (isPlay.value)
                        Icons.Rounded.Pause
                    else
                        Icons.Rounded.PlayArrow,
                    contentDescription = "play/pause icon",
                    tint = onBackgroundColorAnimated.value,
                    modifier = Modifier
                        .size(36.dp)
                )
            }
        )

        IconButton(
            onClick = {
                haptic.performHapticFeedback(
                    HapticFeedbackType.ContextClick
                )
                onNext()
            },
            enabled = isLastTrack.value.not()
        ) {
            Icon(
                imageVector = Icons.Rounded.SkipNext,
                contentDescription = "next icon",
                tint = nextTrackLoadedColorState,
                modifier = Modifier
                    .size(34.dp)
            )
        }
    }
}

@Composable
fun ColoredScaffoldState.BottomControls(
    modifier: Modifier,
    viewModel: AudioPlayerViewModel,
    isLyricsOpen: MutableState<Boolean>
) {
    val coroutineScope = rememberCoroutineScope()
    val repeatMode by viewModel.repeatMode.collectAsStateWithLifecycle()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = { }
        ) {
            Icon(
                painter = painterResource(R.drawable.timer_icon),
                contentDescription = "time icon",
                tint = onBackgroundColorAnimated.value
            )
        }

        IconToggleButton(
            checked = isLyricsOpen.value,
            onCheckedChange = {
                isLyricsOpen.value = !isLyricsOpen.value

                coroutineScope.launch {
                    viewModel.fetchCurrentTrackWithLyrics()
                }
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.lyrics_icon),
                contentDescription = "time icon",
                tint = onBackgroundColorAnimated.value
            )
        }

        IconButton(
            onClick = {
                viewModel.nextRepeatMode()
            }
        ) {
            Icon(
                painter = when(repeatMode) {
                    PlayerState.RepeatMode.Single -> painterResource(R.drawable.repeat_icon_1)
                    PlayerState.RepeatMode.Playlist -> painterResource(R.drawable.repeat_icon)
                    PlayerState.RepeatMode.Forward -> painterResource(R.drawable.repeat_off)
                },
                contentDescription = "time icon",
                tint = onBackgroundColorAnimated.value
            )
        }
    }
}

@Composable
fun ColoredScaffoldState.LyricsDrawer(
    viewModel: AudioPlayerViewModel
) {
    val density = LocalDensity.current
    val trackName by remember {
        derivedStateOf {
            viewModel.track.value?.name ?: "unknown"
        }
    }
    val track by viewModel.track.collectAsStateWithLifecycle()
    val lyrics by remember {
        derivedStateOf {
            track?.lyrics
        }
    }

    when {
        lyrics == null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = onBackgroundColorAnimated.value
                )
            }
        }
        lyrics?.syncedText?.isNotEmpty() ?: false -> {
            val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()
            val lazyListState = rememberLazyListState()
            val syncedTextPairs = remember {
                return@remember lyrics!!.syncedText!!.map { it.key to it.value.trim() }.toMutableList().apply {
                    add(Long.MAX_VALUE to "")
                }
            }
            val syncedTextSizes = remember { mutableMapOf<Long, IntSize>() }

            var columnSize by remember { mutableStateOf(IntSize.Zero) }
            var currentIndex by remember { mutableIntStateOf(-1) }

            LaunchedEffect(currentPosition) {
                currentIndex = findCurrentIndex(currentPosition, syncedTextPairs)
            }

            LaunchedEffect(currentIndex) {
                if (currentIndex in syncedTextPairs.indices) {
                    lazyListState.animateScrollToItem(currentIndex + 1,
                        -(columnSize.height / 2) + ((syncedTextSizes[syncedTextPairs[currentIndex].first]?.height ?: 0) / 2)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .onSizeChanged {
                        columnSize = it
                    },
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(70.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(Modifier.height(with(density) { columnSize.height.toDp() / 2 }))
                }

                itemsIndexed(
                    items = syncedTextPairs,
                    key = { _, item -> item.first }
                ) { index, item ->
                    Text(
                        text = item.second,
                        fontSize = 32.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.W700,
                        textAlign = TextAlign.Center,
                        color = if (currentIndex == index) bodyTextOnBackgroundAnimated.value else bodyTextOnBackgroundAnimated.value.copy(.2f),
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                viewModel.seek(item.first)
                            },
                        onTextLayout = {
                            syncedTextSizes.put(item.first, it.size)
                        }
                    )
                }

                item {
                    Text(
                        "Lyrics provider ${lyrics?.provider} open library",
                        fontSize = 13.sp,
                        color = bodyTextOnBackgroundAnimated.value
                    )

                    Spacer(Modifier.height(100.dp))
                }
            }
        }
        lyrics?.plainText?.isNotEmpty() ?: false -> {
            Column(
                modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 100.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = trackName,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.W700,
                    color = bodyTextOnBackgroundAnimated.value
                )

                Text(
                    text = lyrics!!.plainText!!,
                    modifier = Modifier,
                    color = bodyTextOnBackgroundAnimated.value
                )

                Text(
                    "Lyrics provider ${lyrics?.provider} open library",
                    fontSize = 13.sp,
                    color = bodyTextOnBackgroundAnimated.value
                )
            }
        }
        else -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Error,
                        modifier = Modifier
                            .size(52.dp),
                        contentDescription = "Lyrics warning",
                        tint = onBackgroundColor.value
                    )

                    Text(
                        text = "Не удалось найти текст для этого трека",
                        color = onBackgroundColor.value,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private fun findCurrentIndex(position: Long, pairs: List<Pair<Long, String>>): Int {
    var low = 0
    var high = pairs.size - 2

    while (low <= high) {
        val mid = (low + high) / 2
        if (position in pairs[mid].first..pairs[mid + 1].first) {
            return mid
        } else if (position < pairs[mid].first) {
            high = mid - 1
        } else {
            low = mid + 1
        }
    }
    return -1
}