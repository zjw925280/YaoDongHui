package net.knowfx.yaodonghui.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.knowfx.yaodonghui.ext.trueLet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseListData
import java.lang.ref.WeakReference


class AutoScrollRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    companion object {
        /**自动滑动的步时*/
        const val TIME_AUTO_POLL = 16L
    }

    /**滚动速度变量*/
    private var scrollSpeed = ScrollSpeed.SPEED_MIDDLE.value

    /**列表自动从头部向下填充的缓冲区大小*/
    private var offsetCount = 2

    /**是否正在自动轮询*/
    private var running = false

    /**是否可以自动轮询*/
    private var canRun = false

    /**自动轮询的任务*/
    private val autoPollTask: AutoPollTask = AutoPollTask(this)

    /**轮询时，上次从顶部移除的数据列表，用于添加到列表最后*/
    private val mLastDeleteList = ArrayList<BaseListData>()

    /**轮询任务对象*/
    inner class AutoPollTask(reference: AutoScrollRecyclerView) : Runnable {
        private val mReference: WeakReference<AutoScrollRecyclerView>

        //使用弱引用持有外部类引用->防止内存泄漏
        init {
            mReference = WeakReference<AutoScrollRecyclerView>(reference)
        }

        override fun run() {
            isVisBottom()
            if (running && canRun) {
                //竖直移动
                scrollBy(0, scrollSpeed)
                postOnAnimationDelayed(autoPollTask, TIME_AUTO_POLL)
            }
        }
    }


    /**是否滚动到最下部*/
    private fun isVisBottom() {
        val manager = layoutManager as LinearLayoutManager
        //屏幕中最后一个可见子项的position
        val lastVisibleItemPosition = manager.findLastVisibleItemPosition()
        //当前屏幕所看到的子项个数
        val visibleItemCount = manager.childCount
        //当前RecyclerView的所有子项个数
        val totalItemCount = manager.itemCount
        //RecyclerView的滑动状态
        val state = scrollState
        canRun =
            !(visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == SCROLL_STATE_IDLE)
    }

    private fun start() {
        if (running) return
        canRun = true
        running = true
        postDelayed(autoPollTask, TIME_AUTO_POLL)
    }

    override fun onChildAttachedToWindow(child: View) {
        adapter?.apply {
            this as CommonListAdapter<*>
            (getChildAdapterPosition(child) == getDataList<BaseListData>().size - 2).trueLet {
                //如果倒数第二个item，滑入界面，则从上次移除的头部数据或者当前正在显示的头部数据中向最后添加缓冲个数据源
                CoroutineScope(Dispatchers.Main).launch {
                    addLast()
                }
            }
        }
        super.onChildAttachedToWindow(child)
    }

    override fun onChildDetachedFromWindow(child: View) {
        adapter?.apply {
            this as CommonListAdapter<*>
            (getChildAdapterPosition(child) == offsetCount - 1).trueLet {
                CoroutineScope(Dispatchers.Main).launch {
                    CoroutineScope(Dispatchers.Main).launch {
                        delHead()
                    }
                }
            }
        }
        super.onChildDetachedFromWindow(child)
    }

    /**从列表头部或者上次移除的头部数据[mLastDeleteList]添加数据到[getAdapter]数据最后*/
    private fun addLast() {
        stop()
        adapter?.apply {
            this as CommonListAdapter<*>
            val list = getDataList<BaseListData>()
            if (mLastDeleteList.isNotEmpty()) {
                addDataListToEnd(mLastDeleteList)
                mLastDeleteList.clear()
            } else {
                val result = ArrayList<BaseListData>()
                for (i in 0 until offsetCount) {
                    result.add(list[i])
                }
                addDataListToEnd(result)
            }
            start()
        }
    }

    /**从列表头部移除缓冲个数据并存储在[mLastDeleteList]中*/
    private fun delHead() {
        stop()
        adapter?.apply {
            this as CommonListAdapter<*>
            mLastDeleteList.addAll(getDataList<BaseListData>().subList(0, offsetCount))
            delDataRange(0, offsetCount)
            start()

        }
    }

    private fun stop() {
        running = false
        removeCallbacks(autoPollTask)
    }

    /**开始自动滚动*/
    fun startAutoScrolling(): AutoScrollRecyclerView {
        (running).trueLet {
            return this@AutoScrollRecyclerView
        }
        start()
        return this
    }

    /**设置缓冲数量*/
    fun setOffsetCount(count: Int): AutoScrollRecyclerView {
        offsetCount = count
        return this
    }

    /**设置滚动速度*/
    fun setSpeed(speed: ScrollSpeed): AutoScrollRecyclerView {
        scrollSpeed = speed.value
        return this
    }

    /**停止自动滚动*/
    fun stopAutoScrolling(): AutoScrollRecyclerView {
        running.trueLet {
            stop()
        }
        return this
    }

    /**是否在滚动*/
    fun isAutoScrolling(): Boolean {
        return running
    }

    enum class ScrollSpeed(val value: Int) {
        SPEED_SLOW(2),
        SPEED_MIDDLE(4),
        SPEED_FAST(8),
    }
}