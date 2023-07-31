package net.knowfx.yaodonghui.utils

import com.scwang.smartrefresh.layout.SmartRefreshLayout

class RefreshController(
    private val refreshLayout: SmartRefreshLayout,
    private val isEnableRefresh: Boolean,
    private val mCallback: (page: Int) -> Unit
) {
    private var mPage = 1

    init {
        refreshLayout.setEnableRefresh(isEnableRefresh)
        refreshLayout.setEnableLoadMore(false)
        refreshLayout.setOnRefreshListener {
            mPage = 1
            mCallback.invoke(mPage)
        }
        refreshLayout.setOnLoadMoreListener {
            mPage += 1
            mCallback.invoke(mPage)
        }
    }

    fun refresh(): RefreshController {
        if (isEnableRefresh) {
            refreshLayout.autoRefresh()
        } else {
            mPage = 1
            mCallback.invoke(mPage)
        }
        return this
    }

    fun setEnableLoadMore(flag: Boolean) {
        refreshLayout.setEnableLoadMore(flag)
    }
}