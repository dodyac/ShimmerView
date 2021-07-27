package com.acxdev.shimmer.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat.getColor
import com.acxdev.shimmer.ShimmerController
import com.acxdev.commonFunction.util.shimmer.ShimmerView
import com.acxdev.shimmer.Constant
import com.acxdev.shimmer.R

class ShimmerImage : AppCompatImageView, ShimmerView {

    private var shimmerController: ShimmerController? = null
    private var defaultColorResource = 0

    constructor(context: Context) : super(context) { init(context, null) }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init(context, attrs) }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) { init(context, attrs) }

    private fun init(context: Context, attrs: AttributeSet?) {
        shimmerController = ShimmerController(this)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShimmerImage, 0, 0)
        shimmerController!!.setUseGradient(typedArray.getBoolean(R.styleable.ShimmerImage_shimmerImageGradientColor, Constant.USE_GRADIENT_DEFAULT))
        shimmerController!!.setCorners(typedArray.getInt(R.styleable.ShimmerImage_shimmerImageCornerRadius, Constant.CORNER_DEFAULT))
        defaultColorResource = typedArray.getColor(R.styleable.ShimmerImage_shimmerImageColor, getColor(context, R.color.default_color))
        typedArray.recycle()
        showShimmer()
    }

    fun showShimmer() {
        if (drawable != null) {
            super.setImageDrawable(null)
            shimmerController!!.startLoading()
        }
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        shimmerController!!.onSizeChanged()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        shimmerController!!.onDraw(canvas)
    }

    override fun setRectColor(rectPaint: Paint?) { rectPaint?.color = defaultColorResource }

    override fun valueSet(): Boolean { return drawable != null }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        shimmerController!!.stopLoading()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        shimmerController!!.stopLoading()
    }

    override fun setImageIcon(icon: Icon?) {
        super.setImageIcon(icon)
        shimmerController!!.stopLoading()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        shimmerController!!.stopLoading()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        shimmerController!!.removeAnimatorUpdateListener()
    }
}