package com.inwave.control

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

private val borderSize = 10.dp

@Composable
fun MarqueeText(
    containerModifier: Modifier = Modifier,
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    textAlign: Alignment = Alignment.CenterStart,
    lineHeight: TextUnit = TextUnit.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    onClick: (() -> Unit)? = null
) {
    val textLayoutResult: MutableState<TextLayoutResult?> = remember { mutableStateOf(null) }

    MarqueeText(
        modifier = containerModifier,
        textLayoutResult = textLayoutResult.value,
        contentAlignment = textAlign,
        onClick = onClick
    ) {
        Text(
            text = text,
            modifier = modifier,
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            lineHeight = lineHeight,
            maxLines = maxLines,
            onTextLayout = {
                textLayoutResult.value = it
            }
        )
    }
}

@Composable
fun MarqueeText(
    modifier: Modifier = Modifier,
    textLayoutResult: TextLayoutResult?,
    contentAlignment: Alignment = Alignment.CenterStart,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
            .onSizeChanged {
                boxSize = it
            }
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()

                textLayoutResult?.size?.width?.let {
                    if (boxSize.width < it) {
                        with(density) {
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    0f to Color.Transparent,
                                    borderSize / boxSize.width.toDp() to Color.White,
                                    1f - borderSize / boxSize.width.toDp() to Color.White,
                                    1f to Color.Transparent
                                ),
                                blendMode = BlendMode.DstIn
                            )
                        }
                    }
                }
            }
            .basicMarquee(
                iterations = Int.MAX_VALUE,
                repeatDelayMillis = 0
            ),
        contentAlignment = contentAlignment
    ) {
        content()
    }
}