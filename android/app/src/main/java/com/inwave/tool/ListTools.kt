package com.inwave.tool

fun List<Int>.normalize(): List<Float> {
    val maxElem = this.max()

    return this.map {
        it.toFloat() / maxElem
    }
}