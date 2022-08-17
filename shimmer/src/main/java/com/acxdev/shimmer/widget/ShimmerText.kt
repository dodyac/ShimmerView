package com.acxdev.shimmer.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.widget.doOnTextChanged
import com.acxdev.shimmer.ShimmerListener
import com.acxdev.shimmer.R
import com.acxdev.shimmer.ShimmerController
import com.acxdev.shimmer.ShimmerViewConstant

class ShimmerText : AppCompatTextView, ShimmerListener {

    private var shimmerController: ShimmerController? = null
    private var defaultColorResource = 0
    private var darkerColorResource = 0
    private var placeHolderText = ""

    constructor(context: Context) : super(context) { init(context, null) }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init(context, attrs) }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) { init(context, attrs) }

    private fun init(context: Context, attrs: AttributeSet?) {
        shimmerController = ShimmerController(this)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShimmerText, 0, 0)
        shimmerController?.let {
            it.setWidthWeight(typedArray.getFloat(R.styleable.ShimmerText_shimmerTextWidth, ShimmerViewConstant.MAX_WEIGHT))
            it.setHeightWeight(typedArray.getFloat(R.styleable.ShimmerText_shimmerTextHeight, ShimmerViewConstant.MAX_WEIGHT))
            it.setUseGradient(typedArray.getBoolean(R.styleable.ShimmerText_shimmerTextGradientColor, ShimmerViewConstant.USE_GRADIENT_DEFAULT))
            it.setCorners(typedArray.getInt(R.styleable.ShimmerText_shimmerTextCornerRadius, ShimmerViewConstant.CORNER_DEFAULT))
        }
        defaultColorResource = typedArray.getColor(R.styleable.ShimmerText_shimmerTextColor, getColor(context, R.color.default_color))
        darkerColorResource = typedArray.getColor(R.styleable.ShimmerText_shimmerTextColor, getColor(context, R.color.darker_color))
        typedArray.recycle()

        measurePlaceholderWidth(text.toString())
        placeHolderText = text.toString()
        showShimmer()
        doOnTextChanged { text, _, _, _ ->
            if(text.isNullOrEmpty()) {
                measurePlaceholderWidth(placeHolderText)
            } else {
                measurePlaceholderWidth(text.toString())
            }
        }
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        shimmerController?.onSizeChanged()
    }

    fun showShimmer() {
        if (text.isNotEmpty()) {
            text = null
            shimmerController?.startLoading()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        shimmerController?.onDraw(
            canvas, compoundPaddingLeft.toFloat(),
            compoundPaddingTop.toFloat(),
            compoundPaddingRight.toFloat(),
            compoundPaddingBottom.toFloat()
        )
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        shimmerController?.stopLoading()
    }

    override fun setRectColor(rectPaint: Paint?) {
        val typeface = typeface
        rectPaint?.color = if (typeface.style == Typeface.BOLD) darkerColorResource
        else defaultColorResource
    }

    override fun valueSet(): Boolean { return text.isNotEmpty() }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        shimmerController?.removeAnimatorUpdateListener()
    }

    private fun measurePlaceholderWidth(text: String) {
        val measuredWidth = paint.measureText(text)
        minimumWidth = measuredWidth.toInt()
    }
}