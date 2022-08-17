package com.acxdev.shimmer.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat.getColor
import com.acxdev.commonFunction.util.shimmer.ShimmerView
import com.acxdev.shimmer.R
import com.acxdev.shimmer.ShimmerController
import com.acxdev.shimmer.ShimmerViewConstant


class ShimmerText : AppCompatTextView, ShimmerView {

    private var shimmerController: ShimmerController? = null
    private var defaultColorResource = 0
    private var darkerColorResource = 0
    private val NO_PLACEHOLDER = -1
    private var placeholderText: String? = null

    constructor(context: Context) : super(context) { init(context, null) }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init(context, attrs) }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) { init(context, attrs) }

    private fun init(context: Context, attrs: AttributeSet?) {
        shimmerController = ShimmerController(this)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShimmerText, 0, 0)
        shimmerController!!.setWidthWeight(typedArray.getFloat(R.styleable.ShimmerText_shimmerTextWidth, ShimmerViewConstant.MAX_WEIGHT))
        shimmerController!!.setHeightWeight(typedArray.getFloat(R.styleable.ShimmerText_shimmerTextHeight, ShimmerViewConstant.MAX_WEIGHT))
        shimmerController!!.setUseGradient(typedArray.getBoolean(R.styleable.ShimmerText_shimmerTextGradientColor, ShimmerViewConstant.USE_GRADIENT_DEFAULT))
        shimmerController!!.setCorners(typedArray.getInt(R.styleable.ShimmerText_shimmerTextCornerRadius, ShimmerViewConstant.CORNER_DEFAULT))
        defaultColorResource = typedArray.getColor(R.styleable.ShimmerText_shimmerTextColor, getColor(context, R.color.default_color))
        darkerColorResource = typedArray.getColor(R.styleable.ShimmerText_shimmerTextColor, getColor(context, R.color.darker_color))
        typedArray.recycle()
        showShimmer()
        checkPlaceholderAttributes(typedArray)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        shimmerController!!.onSizeChanged()
    }

    fun showShimmer() {
        if (!TextUtils.isEmpty(text)) {
            super.setText(null)
            shimmerController!!.startLoading()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        shimmerController!!.onDraw(
            canvas, compoundPaddingLeft.toFloat(),
            compoundPaddingTop.toFloat(),
            compoundPaddingRight.toFloat(),
            compoundPaddingBottom.toFloat()
        )
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        if (shimmerController != null) shimmerController!!.stopLoading()
    }

    override fun setRectColor(rectPaint: Paint?) {
        val typeface: Typeface = typeface
        if (typeface.style == Typeface.BOLD) rectPaint?.color = darkerColorResource
        else rectPaint?.color = defaultColorResource
    }

    override fun valueSet(): Boolean { return !TextUtils.isEmpty(text) }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        shimmerController!!.removeAnimatorUpdateListener()
    }

    private fun checkPlaceholderAttributes(typedArray: TypedArray) {
        val placeholderTextRes = typedArray.getResourceId(R.styleable.ShimmerText_shimmerTextPlaceholder_resource, NO_PLACEHOLDER)
        val stringArgument = typedArray.getNonResourceString(R.styleable.ShimmerText_shimmerTextPlaceholder_string_argument)
        if (placeholderTextRes != NO_PLACEHOLDER) {
            var argument: Any? = null
            if (stringArgument != null) {
                argument = stringArgument
            }
            placeholderText = resources.getString(placeholderTextRes, argument)
            measurePlaceholderTextAndSetMinWidth()
        }
        typedArray.recycle()
    }

    private fun measurePlaceholderTextAndSetMinWidth() {
        if (placeholderText == null) {
            minimumWidth = 0
            return
        }
        val measuredWidth = paint.measureText(placeholderText)
        minimumWidth = measuredWidth.toInt()
    }

    fun setPlaceholderText(text: String) {
        placeholderText = text
        measurePlaceholderTextAndSetMinWidth()
    }
}