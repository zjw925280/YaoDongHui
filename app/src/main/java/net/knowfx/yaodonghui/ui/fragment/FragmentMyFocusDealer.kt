package net.knowfx.yaodonghui.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseSelectFragment
import net.knowfx.yaodonghui.databinding.LayoutSingleListBinding
import net.knowfx.yaodonghui.entities.BrokerContentData
import net.knowfx.yaodonghui.entities.BrokerData
import net.knowfx.yaodonghui.entities.BrokerListData
import net.knowfx.yaodonghui.entities.FocusBrokerData
import net.knowfx.yaodonghui.ext.bindController
import net.knowfx.yaodonghui.ext.getController
import net.knowfx.yaodonghui.ext.getSplitString
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setCanLoadMore
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.unBindController
import net.knowfx.yaodonghui.ext.visible
import net.knowfx.yaodonghui.ui.activity.BrokerContentActivity
import net.knowfx.yaodonghui.ui.activity.MyFocusActivity
import net.knowfx.yaodonghui.utils.CommonMarginDecoration
import net.knowfx.yaodonghui.viewModels.FocusViewModel

class FragmentMyFocusDealer : BaseSelectFragment() {
    private val mBinding = lazy { LayoutSingleListBinding.inflate(layoutInflater) }
    private val mModel = lazy { ViewModelProvider(this)[FocusViewModel::class.java] }
    private val mDataList = ArrayList<FocusBrokerData.ItemData>()
    private val mSelectedList = ArrayList<Int>()
    private val mAdapter = CommonListAdapter<FocusBrokerData.ItemData> { _, data, _ ->
        data as FocusBrokerData.ItemData
        if (mIsSelectMode) {
            if (mSelectedList.contains(data.attentionId)) {
                mSelectedList.remove(data.attentionId)
            } else {
                mSelectedList.add(data.attentionId)
            }
            commitCount()
        } else {
            jumpToTarget(
                requireActivity(),
                BrokerContentActivity::class.java,
                hashMapOf(Pair("brokerId", data.modelId))
            )
        }
    }

    private var mIsSelectMode = false

    override fun setSelectMode(isSelect: Boolean) {
        if (mIsSelectMode == isSelect) {
            return
        }
        mIsSelectMode = isSelect
        switchMode()
    }

    override fun selectAll(selected: Boolean) {
        if (mIsSelectMode) {
            mSelectedList.clear()
            for (i in 0 until mDataList.size) {
                mDataList[i].apply {
                    isSelected = selected
                    if (selected) mSelectedList.add(attentionId)
                    mAdapter.updateData(this, i)
                }
            }
            commitCount()
        }
    }

    override fun getSelectedList() = mSelectedList

    override fun finishUnFocused() {
        val indexList = ArrayList<Int>()
        for (i in (0 until mDataList.size).reversed()) {
            if (mSelectedList.contains(mDataList[i].attentionId)) {
                indexList.add(i)
            }
        }
        indexList.forEach {
            mDataList.removeAt(it)
            mAdapter.delSingleData(it)
        }
        mSelectedList.clear()
    }

    private fun switchMode() {
        mDataList.forEach {
            it.isSelectMode = mIsSelectMode
            if (!mIsSelectMode) {
                it.isSelected = false
            }
        }
        mAdapter.putData(mDataList)
    }

    private fun commitCount() {
        (requireActivity() as MyFocusActivity).updateCount(
            mSelectedList.size,
            mSelectedList.size == mDataList.size
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initObservers()
        initViews()
        return mBinding.value.root
    }

    private fun initObservers() {
        mModel.value.dealerList.observe(this) {
            it?.apply {
                result(FocusBrokerData(), error = { msg -> msg.toast() }, success = { data ->
                    if (mIsSelectMode) {
                        data.list.forEach { item ->
                            item.isSelectMode = true
                        }
                    }
                    if (data.isFirstPage) {
                        mDataList.clear()
                        mAdapter.putData(dataList = data.list)
                    } else {
                        mAdapter.addDataListToEnd(dataList = data.list)
                    }
                    mDataList.addAll(data.list)
                    if (mDataList.isEmpty()) {
                        mBinding.value.layoutEmpty.root.visible()
                    } else {
                        mBinding.value.layoutEmpty.root.gone()
                    }
                    mBinding.value.refreshLayout.setCanLoadMore(!data.isLastPage)
                })
            }
        }
    }

    private fun initViews() {
        mBinding.value.apply {
            recyclerView.layoutManager = LinearLayoutManager(requireActivity())
            val gap = resources.getDimensionPixelOffset(R.dimen.dp_24)
            recyclerView.addItemDecoration(CommonMarginDecoration(gap, 0, 1, true))
            recyclerView.adapter = mAdapter
            refreshLayout.bindController(true) { page ->
                //请求接口
                mModel.value.getFocusDealer(page)
            }
            refreshLayout.getController()?.refresh()

            layoutEmpty.noDataIv.setImageLevel(4)
            layoutEmpty.noDataTv.text = getString(R.string.string_no_data_focus)
            layoutEmpty.noDataIv.setOnclick {
                layoutEmpty.root.gone()
                refreshLayout.getController()?.refresh()
            }
        }
    }

    override fun onDestroy() {
        mBinding.value.refreshLayout.unBindController()
        super.onDestroy()
    }
}