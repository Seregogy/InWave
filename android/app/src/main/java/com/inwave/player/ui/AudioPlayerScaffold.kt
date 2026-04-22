package com.inwave.player.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
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
import androidx.compose.ui.graphics.Color
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
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AudioPlayerScaffold(
    viewModel: AudioPlayerViewModel,
    innerPadding: PaddingValues,
    navController: NavHostController,
    hazeState: HazeState,
    content: @Composable (sheetPeekHeight: Dp, innerPadding: PaddingValues) -> Unit
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
            yCurrentOffset.value / (screenHeight - bottomSectionHeightPx)
        }
    }

    val blurTargetMiniPlayerAlpha by remember {
        derivedStateOf {
            1f - ((yCurrentOffset.value) / alphaStateThreshold).coerceIn(0f..1f)
        }
    }

    val sheetPeekHeight = bottomSectionHeight.value + innerPadding.calculateBottomPadding()
    BottomSheetScaffold(
        sheetPeekHeight = sheetPeekHeight,
        scaffoldState = bottomSheetState,
        sheetDragHandle = { },
        sheetShape = RoundedCornerShape(0.dp),
        sheetContainerColor = Color.Transparent,
        sheetContent = {
            BottomSheetAudioPlayer(
                bottomSectionHeight = bottomSectionHeight,
                innerPadding = innerPadding,
                viewModel = viewModel,
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                targetMiniPlayerAlpha = targetMiniPlayerAlpha,
                blurTargetMiniPlayerAlpha = blurTargetMiniPlayerAlpha,
                hazeState = hazeState,
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
        content(sheetPeekHeight, paddingValues)
    }
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun BottomSheetAudioPlayer(
    bottomSectionHeight: MutableState<Dp>,
    innerPadding: PaddingValues,
    viewModel: AudioPlayerViewModel,
    modifier: Modifier,
    targetMiniPlayerAlpha: State<Float>,
    blurTargetMiniPlayerAlpha: Float,
    hazeState: HazeState,
    onExpandRequest: () -> Unit = { },
    onCollapseRequest: () -> Unit = { },
    onReleaseClick: (albumId: String) -> Unit,
    onArtistClick: (artistId: String) -> Unit
) {
    val density = LocalDensity.current

    val coloredScaffoldState = rememberColoredScaffoldState {
        viewModel.imagePaletteExtractor.palette.collectAsState()
    }

    val bottomBarShown by remember {
        derivedStateOf {
            targetMiniPlayerAlpha.value > 0.94f
        }
    }

    Box {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .alpha(blurTargetMiniPlayerAlpha)
        )

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
            Column(
                modifier = Modifier
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .alpha(targetMiniPlayerAlpha.value)
                    .align(Alignment.TopCenter)
                    .then(
                        if (targetMiniPlayerAlpha.value > 0.99f) {
                            Modifier
                                .hazeEffect(hazeState, HazeMaterials.thin(Color.Black))
                        } else {
                            Modifier
                        }
                    )
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
                    .background(additionalHorizontalGradientBrush.value)
            ) {
                MiniAudioPlayer(
                    viewModel = viewModel,
                    onExpandRequest = onExpandRequest
                )
            }
        }
    }
}