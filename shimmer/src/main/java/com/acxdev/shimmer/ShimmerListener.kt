package com.acxdev.shimmer

import android.graphics.Paint

interface ShimmerListener {
    fun setRectColor(rectPaint: Paint?)

    fun invalidate()

    fun valueSet(): Boolean
}