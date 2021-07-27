package com.acxdev.commonFunction.util.shimmer

import android.graphics.Paint

interface ShimmerView {
    fun setRectColor(rectPaint: Paint?)

    fun invalidate()

    fun valueSet(): Boolean
}