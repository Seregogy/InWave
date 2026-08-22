package com.inwave.control

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.inwave.tool.normalize
import java.lang.Math.toDegrees
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin

//TODO: сделать colorScheme
@Composable
fun RadarChart(
    modifier: Modifier = Modifier,
    chart: List<Pair<String, Int>>,
    radius: Float = 300f
) {
    val textMeasurer = rememberTextMeasurer()

    val pointAmount = chart.size
    val angle = 2 * Math.PI / pointAmount

    val pathEffect = PathEffect.dashPathEffect(
        intervals = floatArrayOf(20f, 15f),
        phase = 0f
    )

    Canvas(modifier.fillMaxSize()) {
        val center = Offset(
            x = size.width / 2,
            y = size.height / 2
        )

        val points = (0..<pointAmount).map {
            Offset(
                x = cos(it * angle).toFloat(),
                y = sin(it * angle).toFloat()
            )
        }

        val pointsWithWeights = points.zip(
            chart.map { it.second }.toList().normalize()
        ).map { (point, weight) ->
            Offset(
                x = point.x * radius * weight + center.x,
                y = point.y * radius * weight + center.y
            )
        }

        points.forEach {
            drawLine(
                color = Color.White,
                start = center,
                end = Offset(
                    x = it.x * radius + center.x,
                    y = it.y * radius + center.y
                ),
                pathEffect = pathEffect
            )
        }

        (0..3).forEach {
            val radiusMultiplier = 1f - (it.toFloat() / 4)

            drawRound(
                radius = radius * radiusMultiplier,
                center = center,
                points = points,
                circleColor = Color.White,
                pointColor = Color.White,
                strokeWidth = if (it != 0) 2f else 3f,
                pathEffect = pathEffect,
                dashStroke = it != 0
            )
        }

        val polygonPath = Path().apply {
            pointsWithWeights.first().let {
                moveTo(it.x, it.y)
            }

            pointsWithWeights.forEach { point ->
                lineTo(
                    x = point.x,
                    y = point.y
                )
            }

            close()
        }

        drawPath(
            path = polygonPath,
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Green.copy(.05f),
                    Color.Green.copy(.5f)
                )
            ),
            style = Fill
        )

        drawPath(
            path = polygonPath,
            color = Color.White,
            style = Stroke(
                width = 3f
            )
        )

        points.zip(
            chart.map { it.first }
        ).forEach { (point, text) ->

            val textStyle = TextStyle(
                color = Color.White,
                fontWeight = FontWeight.W600,
                fontSize = 7.sp
            )

            val textLayoutResult = textMeasurer.measure(
                text = text,
                style = textStyle
            )

            val textSize = textLayoutResult.size

            val angle = if ((point.x > 0 && point.y > 0) || (point.x < 0 && point.y > 0))
                toDegrees(acos(point.x.toDouble())).toFloat() + 90
            else
                toDegrees(asin(point.x.toDouble())).toFloat()

            val textOffset = Offset(
                x = -(textSize.width / 2f),
                y = -(textSize.height / 2f)
            )

            val textCenter = Offset(
                x = point.x * radius + center.x,
                y = point.y * radius + center.y
            )

            withTransform({
                translate(textCenter.x, textCenter.y)

                rotate(
                    degrees = angle,
                    pivot = Offset.Zero
                )

                translate(0f, -25f)
            }) {
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = textOffset
                )
            }
        }
    }
}

fun DrawScope.drawRound(
    radius: Float,
    center: Offset,
    points: List<Offset>,
    circleColor: Color,
    pointColor: Color,
    strokeWidth: Float,
    pathEffect: PathEffect,
    dashStroke: Boolean = false
) {
    val path = Path().apply {
        points.first().let {
            moveTo(
                x = it.x * radius + center.x,
                y = it.y * radius + center.y
            )
        }

        points.forEach { point ->
            lineTo(
                x = point.x * radius + center.x,
                y = point.y * radius + center.y
            )
        }

        close()
    }

    drawPath(
        color = circleColor,
        path = path,
        style = if (dashStroke) Stroke(
            width = strokeWidth,
            pathEffect = pathEffect
        ) else Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round
        )
    )

    /*drawPoints(
        points = points.map {
            Offset(
                x = it.x * radius + center.x,
                y = it.y * radius + center.y
            )
        },
        pointMode = PointMode.Points,
        color = Color.Black,
        strokeWidth = 25f,
        cap = StrokeCap.Round
    )*/

    drawPoints(
        points = points.map {
            Offset(
                x = it.x * radius + center.x,
                y = it.y * radius + center.y
            )
        },
        pointMode = PointMode.Points,
        color = pointColor,
        strokeWidth = 15f,
        cap = StrokeCap.Round
    )
}

@Preview(showSystemUi = false)
@Composable
fun RadarChartPreview() {
    val chart = listOf(
        "hip-hop" to 113,
        "alternative" to 12,
        "emo-rock" to 80,
        "country" to 90,
        "pop" to 60,
    )

    Box(Modifier.fillMaxSize()) {
        RadarChart(
            chart = chart,
            radius = 500f
        )
    }
}