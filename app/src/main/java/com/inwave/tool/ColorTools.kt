package com.inwave.tool

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

operator fun Color.times(multiplier: Float): Color {
    return Color(this.red * multiplier, this.green * multiplier, this.blue * multiplier, this.alpha)
}

fun Color.contrast(otherColor: Color): Float {
    val lum1 = this.luminance() + 0.05f
    val lum2 = otherColor.luminance() + 0.05f

    return maxOf(lum1, lum2) / minOf(lum1, lum2)
}

fun Int.contrast(otherColorValue: Int?): Float {
    otherColorValue?.let {
        val lum1 = Color(this).luminance() + 0.05f
        val lum2 = Color(otherColorValue).luminance() + 0.05f

        return maxOf(lum1, lum2) / minOf(lum1, lum2)
    }

    return .0f
}

fun Color.l(): Double {
    return 0.2126 * this.red + 0.7152 * this.green + 0.0722 * this.blue
}