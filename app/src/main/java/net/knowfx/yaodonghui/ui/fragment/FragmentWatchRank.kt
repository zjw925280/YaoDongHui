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
import net.knowfx.yaodonghui.databinding.LayoutSingleListBinding
import net.knowfx.yaodonghui.entities.FocusBrokerData
import net.knowfx.yaodonghui.ext.bindController
import net.knowfx.yaodonghui.ext.getController
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setCanLoadMore
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.unBindController
import net.knowfx.yaodonghui.ui.activity.BrokerContentActivity
import net.knowfx.yaodonghui.utils.CommonMarginDecoration
import net.knowfx.yaodonghui.viewModels.HistoryViewModel

class FragmentWatchRank : Fragment() {
    private val mBinding = lazy { LayoutSingleListBinding.inflate(layoutInflater) }
    private val mModel = lazy { ViewModelProvider(this)[HistoryViewModel::class.java] }
    private val mAdapter = CommonListAdapter<FocusBrokerData.ItemData> { view, data, position ->
        data as FocusBrokerData.ItemData
        jumpToTarget(
            requireActivity(),
            BrokerContentActivity::class.java,
            hashMapOf(Pair("brokerId", data.id))
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
        mModel.value.historyList.observe(this) {
            it?.apply {
                result(FocusBrokerData(), error = { msg -> msg.toast() }, success = { data ->
                    if (data.isFirstPage) {
                        mAdapter.putData(data.list)
                    } else {
                        mAdapter.addDataListToEnd(dataList = data.list)
                    }
                    mBinding.value.refreshLayout.setCanLoadMore(!data.isLastPage)
                })
            }
        }
    }

    private fun initViews() {
        mBinding.value.apply {
            refreshLayout.bindController(true) {
                mModel.value.getSeeHistory(it)
            }
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            val offset = resources.getDimensionPixelOffset(R.dimen.dp_20)
            recyclerView.addItemDecoration(CommonMarginDecoration(offset, 0, 1, true))
            recyclerView.adapter = mAdapter
            refreshLayout.getController()?.refresh()
        }
    }

    override fun onDestroy() {
        mBinding.value.refreshLayout.unBindController()
        super.onDestroy()
    }
}