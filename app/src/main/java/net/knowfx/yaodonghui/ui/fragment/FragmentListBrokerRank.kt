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
import net.knowfx.yaodonghui.entities.BrokerListData
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ui.activity.BrokerContentActivity
import net.knowfx.yaodonghui.utils.CommonMarginDecoration
import net.knowfx.yaodonghui.utils.MyApplication
import net.knowfx.yaodonghui.viewModels.SuperviseViewModel

class FragmentListBrokerRank : Fragment {
    companion object {
        const val TYPE_SUPERVISE_RANK = 0
        const val TYPE_BRAND_RANK = 1
        const val TYPE_BLOCK_RANK = 2
        const val TYPE_DEALER_RANK = 3
    }
    constructor(): super(){
        listType = TYPE_DEALER_RANK
    }

    constructor(type: Int): super(){
        listType = type
    }

    private lateinit var mViewBinding: LayoutSingleListBinding
    private val mViewModel = lazy { ViewModelProvider(this)[SuperviseViewModel::class.java] }
    private val listType: Int
    private val mAdapter = CommonListAdapter<BrokerListData> { _, data, _ ->
        //跳转到券商详情界面的逻辑
        data as BrokerListData
        jumpToTarget(
            requireActivity(),
            BrokerContentActivity::class.java,
            hashMapOf(Pair("brokerId", data.id))
        )
    }
    private var mPage = 1
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mViewBinding = LayoutSingleListBinding.inflate(inflater)
        mViewBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val offset = resources.getDimensionPixelOffset(R.dimen.dp_20)
        mViewBinding.recyclerView.addItemDecoration(CommonMarginDecoration(offset, 0, 1, true))
        mViewBinding.recyclerView.adapter = mAdapter
        mViewBinding.refreshLayout.setEnableRefresh(true)
        mViewBinding.refreshLayout.setEnableLoadMore(false)
        addListeners()
        initObservers()
        mViewBinding.refreshLayout.autoRefresh()
        return mViewBinding.root
    }

    private fun initObservers() {
        mViewModel.value.dealerList.observe(this) {
            mViewBinding.refreshLayout.finishRefresh()
            mViewBinding.refreshLayout.finishLoadMore()
            it?.apply {
                result(ArrayList<BrokerListData>(), error = { msg ->
                    msg.toast()
                }, success = { data ->
                    (listType == TYPE_BLOCK_RANK).trueLet {
                        data.forEach {block->
                            block.isBlock = true
                        }
                        data.sortBy { item-> item.rowNum }
                    }
                    mAdapter.putData(dataList = data)
                })
            }
        }
    }

    private fun addListeners() {
        mViewBinding.refreshLayout.setOnRefreshListener {
            mPage = 1
            requestList()
        }

        mViewBinding.refreshLayout.setOnLoadMoreListener {
            mPage += 1
            requestList()
        }
    }

    private fun requestList() {
        when (listType) {
            TYPE_SUPERVISE_RANK -> mViewModel.value.getSuperviseList(
                mPage,
                MyApplication.COMMON_PAGE_SIZE
            )

            TYPE_BRAND_RANK -> mViewModel.value.getBrandList(mPage, MyApplication.COMMON_PAGE_SIZE)
            TYPE_BLOCK_RANK -> mViewModel.value.getBlockList(mPage, MyApplication.COMMON_PAGE_SIZE)
            TYPE_DEALER_RANK -> mViewModel.value.getDealerList(
                mPage,
                MyApplication.COMMON_PAGE_SIZE
            )

            else -> {}
        }

    }


}