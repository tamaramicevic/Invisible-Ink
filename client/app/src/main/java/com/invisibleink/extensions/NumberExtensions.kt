package com.invisibleink.extensions

fun randomDouble(one: Double = 0.0, two: Double): Double {
    val (min, max) = if (one < two) {
        one to two
    } else {
        two to one
    }
    return min + (0..100).random().div(100.0).times(max.minus(min))
}