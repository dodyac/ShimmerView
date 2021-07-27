package com.acxdev.shimmer.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acxdev.shimmer.Constant
import com.acxdev.shimmer.R
import com.mikelau.views.shimmer.BindViewHolderPlugin
import com.mikelau.views.shimmer.ShimmerAdapter

class ShimmerRecycler : RecyclerView {

    enum class LayoutMangerType { LINEAR_VERTICAL, LINEAR_HORIZONTAL, GRID }

    private var mActualAdapter: Adapter<*>? = null
    private var mShimmerAdapter: ShimmerAdapter? = null
    private var mShimmerLayoutManager: LayoutManager? = null
    private var mActualLayoutManager: LayoutManager? = null
    private var mLayoutMangerType: LayoutMangerType? = null
    private var mCanScroll = false
    private var mLayoutReference = 0
    private var mGridCount = 0
    private var mShimmerAngle = 0
    private var mShimmerColor = 0
    private var mShimmerDuration = 0
    private var mShimmerMaskWidth = 0f
    private var isAnimationReversed = false
    private var mShimmerItemBackground: Drawable? = null

    constructor(context: Context) : super(context) { init(context, null) }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init(context, attrs) }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) { init(context, attrs) }

    private fun init(context: Context, attrs: AttributeSet?) {
        mShimmerAdapter = ShimmerAdapter()
        val a = context.obtainStyledAttributes(attrs, R.styleable.ShimmerRecycler, 0, 0)
        try {
            layoutReference(a.getResourceId(R.styleable.ShimmerRecycler_layout, R.layout.layout_shimmer))
            childCount = a.getInteger(R.styleable.ShimmerRecycler_child, Constant.CHILD_COUNT_DEFAULT)
            setGridChildCount(a.getInteger(R.styleable.ShimmerRecycler_grid, 2))
            when (a.getInteger(R.styleable.ShimmerRecycler_layout_manager, 0)) {
                0 -> layoutManager(LayoutMangerType.LINEAR_VERTICAL)
                1 -> layoutManager(LayoutMangerType.LINEAR_HORIZONTAL)
                2 -> layoutManager(LayoutMangerType.GRID)
                else -> throw IllegalArgumentException("This value for layout manager is not valid!")
            }
            mShimmerAngle = a.getInteger(R.styleable.ShimmerRecycler_angle, Constant.ANGLE_DEFAULT)
            mShimmerColor = a.getColor(R.styleable.ShimmerRecycler_color, getColor(R.color.white))
            mShimmerItemBackground = a.getDrawable(R.styleable.ShimmerRecycler_item_background)
            mShimmerDuration = a.getInteger(R.styleable.ShimmerRecycler_shimmer_duration, 1500)
            mShimmerMaskWidth = a.getFloat(R.styleable.ShimmerRecycler_mask_width, 0.5f)
            isAnimationReversed = a.getBoolean(R.styleable.ShimmerRecycler_reverse_animation, false)
        } finally { a.recycle() }
        setupShimmerProperties()
    }

    private fun setupShimmerProperties() {
        mShimmerAdapter!!.setShimmerAngle(mShimmerAngle)
        mShimmerAdapter!!.setShimmerColor(mShimmerColor)
        mShimmerAdapter!!.setShimmerMaskWidth(mShimmerMaskWidth)
        mShimmerAdapter!!.setShimmerItemBackground(mShimmerItemBackground)
        mShimmerAdapter!!.setShimmerDuration(mShimmerDuration)
        mShimmerAdapter!!.setAnimationReversed(isAnimationReversed)
        showShimmer()
    }

    fun setBindViewHolderPlugin(plugin: BindViewHolderPlugin?) {
        mShimmerAdapter!!.setBindViewHolderPlugin(plugin)
    }

    fun setGridChildCount(count: Int) { mGridCount = count }

    fun layoutManager(type: LayoutMangerType?) { mLayoutMangerType = type }

    fun setChildCount(count: Int) { mShimmerAdapter!!.setMinItemCount(count) }

    fun duration(duration: Int) { mShimmerAdapter!!.setShimmerDuration(duration) }

    fun maskWidth(maskWidth: Float) { mShimmerAdapter!!.setShimmerMaskWidth(maskWidth) }

    fun showShimmer() {
        mCanScroll = false
        if (mShimmerLayoutManager == null) initShimmerManager()
        layoutManager = mShimmerLayoutManager
        adapter = mShimmerAdapter
    }

    fun hideShimmer() {
        mCanScroll = true
        layoutManager = mActualLayoutManager
        adapter = mActualAdapter
    }

    override fun setLayoutManager(manager: LayoutManager?) {
        if (manager == null) mActualLayoutManager = null
        else if (manager !== mShimmerLayoutManager) mActualLayoutManager = manager
        super.setLayoutManager(manager)
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        if (adapter == null) mActualAdapter = null
        else if (adapter !== mShimmerAdapter) mActualAdapter = adapter
        super.setAdapter(adapter)
    }

    fun getActualAdapter(): Adapter<*>? { return mActualAdapter }

    fun getShimmerAdapter(): Adapter<*>? { return mShimmerAdapter }

    fun getLayoutReference(): Int { return mLayoutReference }

    fun layoutReference(mLayoutReference: Int) {
        this.mLayoutReference = mLayoutReference
        mShimmerAdapter!!.setLayoutReference(getLayoutReference())
    }

    private fun initShimmerManager() {
        when (mLayoutMangerType) {
            LayoutMangerType.LINEAR_VERTICAL -> mShimmerLayoutManager = object : LinearLayoutManager(
                context) { override fun canScrollVertically(): Boolean { return mCanScroll } }
            LayoutMangerType.LINEAR_HORIZONTAL -> mShimmerLayoutManager = object : LinearLayoutManager(
                context, HORIZONTAL, false) { override fun canScrollHorizontally(): Boolean { return mCanScroll } }
            LayoutMangerType.GRID -> mShimmerLayoutManager = object : GridLayoutManager(
                context, mGridCount) { override fun canScrollVertically(): Boolean { return mCanScroll } }
        }
    }

    private fun getColor(id: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) context.getColor(id)
        else ContextCompat.getColor(context,id)
    }
}