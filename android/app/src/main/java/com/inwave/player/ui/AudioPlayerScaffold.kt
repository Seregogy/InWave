package com.inwave.player.ui

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.inwave.control.scaffold.color.ColoredScaffold
import com.inwave.control.scaffold.color.rememberColoredScaffoldState
import com.inwave.viewmodel.AudioPlayerViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AudioPlayerScaffold(
    viewModel: AudioPlayerViewModel,
    innerPadding: PaddingValues,
    navController: NavHostController,
    hazeState: HazeState,
    content: @Composable (
        sheetPeekHeight: Dp,
        innerPadding: PaddingValues,
        miniPlayerHeight: State<Dp>
    ) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberBottomSheetScaffoldState()

    val density = LocalDensity.current

    val bottomSectionHeight = remember { mutableStateOf(0.dp) }
    val bottomSectionHeightPx by remember {
        derivedStateOf {
            with(density) {
                bottomSectionHeight.value.roundToPx()
            }
        }
    }

    var allInit by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        allInit = true
    }

    if (bottomSheetState.bottomSheetState.currentValue == SheetValue.Expanded) {
        BackHandler {
            coroutineScope.launch {
                bottomSheetState.bottomSheetState.partialExpand()
            }
        }
    }

    val yCurrentOffset = remember {
        derivedStateOf {
            return@derivedStateOf if (allInit) {
                bottomSheetState.bottomSheetState.requireOffset()
            } else {
                0f
            }
        }
    }

    val screenHeight = with(density) {
        LocalConfiguration.current.screenHeightDp.dp.roundToPx()
    }

    val alphaStateThreshold = with(density) { bottomSectionHeight.value.roundToPx() }
    val targetMiniPlayerAlpha = remember {
        derivedStateOf {
            if (
                bottomSheetState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded &&
                bottomSheetState.bottomSheetState.targetValue == SheetValue.PartiallyExpanded
            ) {
                1f
            } else {
                yCurrentOffset.value / (screenHeight - bottomSectionHeightPx)
            }
        }
    }

    val blurTargetMiniPlayerAlpha by remember {
        derivedStateOf {
            1f - ((yCurrentOffset.value) / alphaStateThreshold).coerceIn(0f..1f)
        }
    }

    val sheetPeekHeight = bottomSectionHeight.value + innerPadding.calculateBottomPadding()
    BottomSheetScaffold(
        sheetShadowElevation = 0.dp,
        sheetTonalElevation = 0.dp,
        sheetPeekHeight = sheetPeekHeight,
        scaffoldState = bottomSheetState,
        sheetDragHandle = { },
        sheetShape = RectangleShape,
        sheetContainerColor = Color.Transparent,
        containerColor = Color.Transparent,
        sheetContent = {
            BottomSheetAudioPlayer(
                bottomSectionHeight = bottomSectionHeight,
                innerPadding = innerPadding,
                viewModel = viewModel,
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                targetMiniPlayerAlpha = targetMiniPlayerAlpha,
                blurTargetMiniPlayerAlpha = blurTargetMiniPlayerAlpha,
                hazeState = hazeState,
                sheetState = bottomSheetState,
                onExpandRequest = {
                    coroutineScope.launch {
                        bottomSheetState.bottomSheetState.expand()
                    }
                },
                onCollapseRequest = {
                    coroutineScope.launch {
                        bottomSheetState.bottomSheetState.partialExpand()
                    }
                },
                onReleaseClick = { releaseId ->
                    coroutineScope.launch {
                        bottomSheetState.bottomSheetState.partialExpand()
                        navController.navigate("/releases/$releaseId")
                    }
                },
                onArtistClick = { artistId ->
                    coroutineScope.launch {
                        bottomSheetState.bottomSheetState.partialExpand()
                        navController.navigate("/artists/$artistId")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(Modifier.fillMaxSize()) {
            content(sheetPeekHeight, paddingValues, bottomSectionHeight)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(100.dp)
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            1f to Color.Black
                        )
                    )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetAudioPlayer(
    bottomSectionHeight: MutableState<Dp>,
    innerPadding: PaddingValues,
    viewModel: AudioPlayerViewModel,
    modifier: Modifier,
    targetMiniPlayerAlpha: State<Float>,
    blurTargetMiniPlayerAlpha: Float,
    hazeState: HazeState,
    sheetState: BottomSheetScaffoldState,

    onExpandRequest: () -> Unit = { },
    onCollapseRequest: () -> Unit = { },
    onReleaseClick: (albumId: String) -> Unit,
    onArtistClick: (artistId: String) -> Unit
) {
    val density = LocalDensity.current

    val coloredScaffoldState = rememberColoredScaffoldState {
        viewModel.imagePaletteExtractor.palette.collectAsState()
    }

    Box {
        Box(
            modifier = Modifier
                .alpha(1f - targetMiniPlayerAlpha.value)
        ) {
            FullAudioPlayer(
                viewModel,
                modifier,
                coloredScaffoldState,
                onCollapseRequest,
                onReleaseClick,
                onArtistClick
            )
        }

        ColoredScaffold(
            state = rememberColoredScaffoldState {
                viewModel.imagePaletteExtractor.palette.collectAsState()
            }
        ) {
            Log.d("AAA", targetMiniPlayerAlpha.value.toString())

            Box(
                modifier = Modifier
                    .alpha(targetMiniPlayerAlpha.value)
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .align(Alignment.TopCenter)
                    .then(
                        if (targetMiniPlayerAlpha.value < .8f)
                            Modifier.pointerInteropFilter { return@pointerInteropFilter false }
                        else
                            Modifier
                    )

                    .onSizeChanged {
                        bottomSectionHeight.value = with(density) {
                            it.height.toDp()
                        }
                    }
            ) {
                MiniAudioPlayer(
                    viewModel = viewModel,
                    fillBrush = additionalHorizontalGradientBrush.value,
                    onExpandRequest = onExpandRequest
                )
            }
        }
    }
}