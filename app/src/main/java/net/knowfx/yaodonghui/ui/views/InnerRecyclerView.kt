package net.knowfx.yaodonghui.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewParent
import android.view.Window
import androidx.recyclerview.widget.RecyclerView
import net.knowfx.yaodonghui.R
import kotlin.math.abs

class InnerRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    private val maxHeight: Int
    init {
        maxHeight = context.resources.displayMetrics.heightPixels - context.resources.getDimensionPixelOffset(
            R.dimen.dp_110)
    }
    private var startX = 0
    private var startY = 0
    private var parentRv: RecyclerView? = null
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                parentRv = getParentRv(parent)
                startX = ev.x.toInt()
                startY = ev.y.toInt()
                parentRv?.apply {
                    requestDisallowInterceptTouchEvent(
                        canScrollVertically(-1)
                                || canScrollVertically(1)
                                || canScrollHorizontally(1)
                                || canScrollHorizontally(-1)
                    )
                }
            }
            MotionEvent.ACTION_MOVE -> {
                parentRv?.apply {
                    val endX = ev.x.toInt()
                    val endY = ev.y.toInt()
                    val disX = abs(endX - startX)
                    val disY = abs(endY - startY)
                    if (disY > disX) {
                        //横向滑动
                        requestDisallowInterceptTouchEvent(true)
                    } else {
                        //纵向滑动
                        if (endY - startY > 0) {
                            requestDisallowInterceptTouchEvent(
                                this@InnerRecyclerView.canScrollVertically(-1)
                            )
                        } else {
                            requestDisallowInterceptTouchEvent(
                                this@InnerRecyclerView.canScrollVertically(1) &&
                                        !canScrollVertically(1)
                            )
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> parent.requestDisallowInterceptTouchEvent(
                false
            )
        }
        return super.onInterceptTouchEvent(ev)
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (heightMode == MeasureSpec.EXACTLY) {
            heightSize = if (heightSize <= maxHeight) heightSize else maxHeight
        }
        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = if (heightSize <= maxHeight) heightSize else maxHeight
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = if (heightSize <= maxHeight) heightSize else maxHeight
        }
        val maxHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode)
        super.onMeasure(widthMeasureSpec, maxHeightMeasureSpec)
    }

    private fun getParentRv(curParent: ViewParent): RecyclerView? {
        return when (curParent) {
            is Window -> {
                null
            }
            is RecyclerView -> {
                curParent
            }
            else -> {
                getParentRv(curParent.parent)
            }
        }
    }
}