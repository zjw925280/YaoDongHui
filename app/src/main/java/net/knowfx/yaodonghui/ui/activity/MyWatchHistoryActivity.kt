package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.databinding.ActivityMyWatchHistoryBinding
import net.knowfx.yaodonghui.ext.setMultipleClick
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.entities.FocusBrokerData
import net.knowfx.yaodonghui.ext.bindController
import net.knowfx.yaodonghui.ext.getController
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setCanLoadMore
import net.knowfx.yaodonghui.ext.showCenterDialog
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.unBindController
import net.knowfx.yaodonghui.ui.dialogs.DialogRemain
import net.knowfx.yaodonghui.utils.CommonMarginDecoration
import net.knowfx.yaodonghui.viewModels.HistoryViewModel

class MyWatchHistoryActivity : BaseActivity() {
    private lateinit var mBinding: ActivityMyWatchHistoryBinding
    private val mModel = lazy { ViewModelProvider(this)[HistoryViewModel::class.java] }
    private val mAdapter = CommonListAdapter<FocusBrokerData.ItemData> { view, data, position ->
        data as FocusBrokerData.ItemData
        jumpToTarget(
            this,
            BrokerContentActivity::class.java,
            hashMapOf(Pair("brokerId", data.id))
        )
    }


    override fun getContentView(): View {
        mBinding = ActivityMyWatchHistoryBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initViewModel() {
        mModel.value.historyList.observe(this) {
            it?.apply {
                result(FocusBrokerData(), error = { msg -> msg.toast() }, success = { data ->
                    if (data.isFirstPage) {
                        mAdapter.putData(data.list)
                    } else {
                        mAdapter.addDataListToEnd(dataList = data.list)
                    }
                    mBinding.layoutList.refreshLayout.setCanLoadMore(!data.isLastPage)
                })
            }
        }
        mModel.value.delResult.observe(this) {
            it?.apply {
                if (isSuccess) {
                    mBinding.layoutList.refreshLayout.getController()?.refresh()
                } else {
                    msg?.toast()
                }
            }
        }
    }


    override fun setData(savedInstanceState: Bundle?) {
        setListeners()
        mBinding.layoutList.apply {
            refreshLayout.bindController(true) {
                mModel.value.getMyHistory(it)
            }
            recyclerView.layoutManager = LinearLayoutManager(this@MyWatchHistoryActivity)
            val offset = resources.getDimensionPixelOffset(R.dimen.dp_20)
            recyclerView.addItemDecoration(CommonMarginDecoration(offset, 0, 1, true))
            recyclerView.adapter = mAdapter
            refreshLayout.getController()?.refresh()
        }
        super.setData(savedInstanceState)
    }

    private fun setListeners() {
        setMultipleClick(mBinding.btnBack, mBinding.btnDelete) {
            when (it) {
                mBinding.btnBack -> {
                    this.finish()
                }

                mBinding.btnDelete -> {
                    //切换删除界面
                    showCenterDialog(
                        DialogRemain(
                            this,
                            content = "确定要删除所有浏览历史吗？",
                            title = "提示"
                        ) {
                            mModel.value.delMyHistory()
                        })
                }

                else -> {}
            }
        }
    }

    override fun onDestroy() {
        mBinding.layoutList.refreshLayout.unBindController()
        super.onDestroy()
    }
}