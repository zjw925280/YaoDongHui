package net.knowfx.yaodonghui.ui.views

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * Created by cpf on 2017/9/7.
 *
 *
 * 描述：listview分割线
 */
open class ListDividerItemDecoration  constructor(
    dividerColor: Int = Color.parseColor("#eaeaea"),
    dividerHeight: Int = 1,
    marginLeft: Int = 0,
    marginRight: Int = 0,
    startPosition: Int = 1
) : ItemDecoration() {
    /**
     * 分割线颜色
     */
    private var mDividerColor = 0

    /**
     * 分割线高度
     */
    private var mDividerHeight = 0

    /**
     * 距离左边
     */
    private var mMarginLeft = 0

    /**
     * 距离右边
     */
    private var mMarginRight = 0

    //是否绘制最后一行
    private var mIsDrawLastLine = false
    private var mPaint: Paint
    private var startPosition = 1

    constructor(marginLeft: Int = 0, marginRight: Int = 0) : this(
        Color.parseColor("#eaeaea"),
        1,
        marginLeft,
        marginRight
    )

    init {
        mDividerColor = dividerColor
        mDividerHeight = dividerHeight
        mMarginLeft = marginLeft
        mMarginRight = marginRight
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.color = mDividerColor
        this.startPosition = startPosition
    }

    fun drawLastLine(isDrawLastLine: Boolean) {
        mIsDrawLastLine = isDrawLastLine
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount - if (mIsDrawLastLine) 0 else 1
        for (i in startPosition until childCount) {
            val child = parent.getChildAt(i)
            val paddingLeft = child.paddingLeft
            val left = child.left + paddingLeft + getMarginLeft(child)
            val right = child.right - mMarginRight
            val top = child.bottom
            val bottom = top + mDividerHeight
            onDrawDivider(c, left, top, right, bottom, child)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect[0, 0, 0] = mDividerHeight
    }

    private fun getMarginLeft(child: View?): Int {
        return mMarginLeft
    }

    private fun onDrawDivider(
        c: Canvas,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        child: View?
    ) {
        c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
    }
}