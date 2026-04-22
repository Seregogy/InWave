package com.inwave.page.main

import android.graphics.BlendMode
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Typeface
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.sp
import com.inwave.control.scaffold.color.ColoredScaffoldState
import kotlin.random.Random

fun androidx.compose.ui.graphics.Color.invert(): androidx.compose.ui.graphics.Color {
    return Color(
        red = 1f - red,
        green = 1f - green,
        blue = 1f - blue,
        alpha = alpha
    )
}

/**
 * @author https://github.com/sinasamaki
 **/
@Composable
fun ColoredScaffoldState.WaterLevel(
    depthMeasurement: String
) = BoxWithConstraints {
    val infiniteTransition = rememberInfiniteTransition()
    val waterLevel by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.505f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val density = LocalDensity.current
    val height = with(density) { maxHeight.roundToPx() }
    val width = with(density) { maxWidth.roundToPx() }

    val currentY = height * waterLevel
    val animatedY by animateFloatAsState(
        targetValue = height * waterLevel,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessVeryLow
        )
    )

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    val haptic = LocalHapticFeedback.current
    LaunchedEffect(isPressed) {
        if (isPressed)
            haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
        else
            haptic.performHapticFeedback(HapticFeedbackType.ToggleOff)
    }

    val aYs = calculateYs(height = height, waterLevel = waterLevel, intensityMultiplier = .1f)
    val aYs2 = calculateYs(height = height, waterLevel = waterLevel, intensityMultiplier = .2f)
    val aYs3 = calculateYs(height = height, waterLevel = waterLevel, intensityMultiplier = .05f)

    Box(Modifier.fillMaxSize()
        .background(backgroundColorAnimated.value)
        .background(additionalVerticalGradientBrush.value)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        isPressed = !isPressed
                    }
                )
            }
    ) {
        Canvas(
            modifier = Modifier
                .graphicsLayer(alpha = 0.99f)
                .fillMaxSize(),
            onDraw = {
                drawPath(
                    path = ayPath(
                        aYs, size, currentY, animatedY
                    ),
                    color = textOnPrimaryOrBackgroundColorAnimated.value
                    /*brush = Brush.verticalGradient(
                        colors = listOf(
                            onBackgroundColorAnimated.value
                        )
                    )*/
                )

                drawPath(
                    path = ayPath(
                        aYs2, size, currentY, animatedY
                    ),
                    alpha = .5f,
                    color = textOnPrimaryOrBackgroundColorAnimated.value,
                )

                drawPath(
                    path = ayPath(
                        aYs3, size, currentY, animatedY
                    ),
                    alpha = .3f,
                    color = textOnPrimaryOrBackgroundColorAnimated.value,
                )

                val paint = androidx.compose.ui.graphics.Paint().asFrameworkPaint()

                paint.apply {
                    isAntiAlias = true
                    textSize = (100 * scale).sp.toPx()
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    color = backgroundColorAnimated.value.invert().toArgb()
                    textAlign = Paint.Align.CENTER

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        paint.blendMode = BlendMode.XOR
                    } else {
                        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.XOR)
                    }
                }

                drawIntoCanvas {
                    it.nativeCanvas.apply {
                        drawText(
                            depthMeasurement,
                            width / 2f,
                            height / 2f + (100.sp.toPx() / 2),
                            paint,
                        )
                    }
                }
            }
        )
    }
}

fun ayPath(aYs: List<Int>, size: Size, currentY: Float, animatedY: Float): Path {
    return Path().apply {
        moveTo(0f, 0f)
        lineTo(0f, animatedY)

        val interval = size.width * (1 / (aYs.size + 1).toFloat())
        aYs.forEachIndexed { index, y ->
            val segmentIndex = (index + 1) / (aYs.size + 1).toFloat()
            val x = size.width * segmentIndex
            cubicTo(
                x1 = if (index == 0) 0f else x - interval / 2f,
                y1 = aYs.getOrNull(index - 1)?.toFloat() ?: currentY,
                x2 = x - interval / 2f,
                y2 = y.toFloat(),
                x3 = x,
                y3 = y.toFloat(),
            )
        }

        cubicTo(
            x1 = size.width - interval / 2f,
            y1 = aYs.last().toFloat(),
            x2 = size.width,
            y2 = animatedY,
            x3 = size.width,
            y3 = animatedY,
        )
        lineTo(size.width, 0f)
        close()
    }
}

@Composable
fun calculateYs(height: Int, waterLevel: Float, intensityMultiplier: Float): List<Int> {
    val total = 3
    return (0..total).map {
        calculateY(height = height, waterLevel = waterLevel,
            ((if (it > total / 2f) total - it else it) / (total / 2f) * 1f) *
                intensityMultiplier
        )
    }.toList()
}

@Composable
fun calculateY(height: Int, waterLevel: Float, intensity: Float): Int {
    var y1 by remember {
        mutableStateOf(0)
    }

    val duration = remember {
        Random.nextInt(500) + 800
    }

    val yNoiseAnimation = rememberInfiniteTransition()
    val yNoise by yNoiseAnimation.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = duration,
                easing = FastOutSlowInEasing,
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(key1 = waterLevel, block = {
        y1 = (waterLevel * height).toInt()
        y1 = (y1 + yNoise).toInt()
    })

    val ay1 by animateIntAsState(
        targetValue = y1,
        animationSpec = spring(
            dampingRatio = 1f - intensity,
            stiffness = Spring.StiffnessVeryLow, //Spring.StiffnessVeryLow
        )
    )

    return ay1
}

internal fun lerp(start: Float, stop: Float, fraction: Float) =
    (start * (1 - fraction) + stop * fraction)