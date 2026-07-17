package com.inwave.tool

import java.text.SimpleDateFormat
import kotlin.math.absoluteValue

fun Number.formatNumber(): String {
    return String.format("%,d", this).replace(",", " ")
}

fun formatMinuteTimer(seconds: Int): String {
    return "${if(seconds < 0) "-" else ""}${(seconds.div(60).absoluteValue).toString().padStart(1, '0')}:${(seconds % 60).absoluteValue.toString().padStart(2, '0')}"
}

fun Long.toDate(format: String = "dd MMMM yyyy"): String {
    return SimpleDateFormat(format).format(this * 1000L)
}