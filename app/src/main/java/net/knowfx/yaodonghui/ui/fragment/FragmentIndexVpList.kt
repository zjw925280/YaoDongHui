package net.knowfx.yaodonghui.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.base.BaseRefreshFragment
import net.knowfx.yaodonghui.databinding.LayoutSingleListBinding
import net.knowfx.yaodonghui.entities.IndexPagerData
import net.knowfx.yaodonghui.ext.bindController
import net.knowfx.yaodonghui.ext.getController
import net.knowfx.yaodonghui.ext.jumpFromPush
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setCanLoadMore
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.unBindController
import net.knowfx.yaodonghui.ui.activity.ArticleContentActivity
import net.knowfx.yaodonghui.utils.MyApplication.COMMON_PAGE_SIZE
import net.knowfx.yaodonghui.viewModels.IndexViewModel
import kotlin.math.max
import kotlin.math.min

class FragmentIndexVpList : BaseRefreshFragment {
    companion object {
        const val CLASS_ID_NEWEST = "0"
        const val CLASS_ID_ORIGINAL = "1"
        const val CLASS_ID_BROKER = "2"
    }

    constructor() : super()
    constructor(id: String) : super() {
        classId = id
    }

    private var classId: String? = null
    private lateinit var mBinding: LayoutSingleListBinding
    private lateinit var mViewModel: IndexViewModel
    private val mAdapter = CommonListAdapter<IndexPagerData.ListData> { _, data, _ ->
        //item点击事件
        data as IndexPagerData.ListData
        //跳转到资讯详情
        requireActivity().jumpFromPush(data.model, data.id)
    }
    override fun refresh() {
        mBinding.refreshLayout.getController()?.refresh()
        setCanScroll(false)
    }

    private var mScrollable = false
    override fun setCanScroll(canScroll: Boolean) {
        mScrollable = canScroll
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = LayoutSingleListBinding.inflate(inflater)
        initViewModel()
        initViews()
        return mBinding.root
    }

    private fun initViews() {
        mBinding.recyclerView.layoutManager = object: LinearLayoutManager(requireContext()){
            override fun canScrollVertically(): Boolean {
                return mScrollable
            }
        }
        mBinding.recyclerView.adapter = mAdapter
        mBinding.refreshLayout.bindController(false) {
            mViewModel.getPagerList(classId ?: "", it, COMMON_PAGE_SIZE)
        }
        mAdapter.putData(ArrayList())
        mBinding.refreshLayout.getController()?.refresh()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(this)[IndexViewModel::class.java]
        mViewModel.pagerListNewest.observe(requireActivity()) {
            it?.apply {
                result(IndexPagerData.PagerListData(), error = { msg ->
                    msg.toast()
                }, success = { data ->
                    mBinding.refreshLayout.finishLoadMore()
                    if (data.isFirstPage) {
                        mAdapter.putData(data.list)
                    } else {
                        mAdapter.addDataListToEnd(data.list)
                    }
                    mBinding.refreshLayout.setCanLoadMore(!data.isLastPage)
                })
            }
        }
        mViewModel.pagerListOrigin.observe(requireActivity()) {
            it?.apply {
                result(IndexPagerData.PagerListData(), error = { msg ->
                    msg.toast()
                }, success = { data ->
                    mBinding.refreshLayout.finishLoadMore()
                    if (data.isFirstPage) {
                        mAdapter.putData(data.list)
                    } else {
                        mAdapter.addDataListToEnd(data.list)
                    }
                    mBinding.refreshLayout.setCanLoadMore(!data.isLastPage)
                })
            }
        }
        mViewModel.pagerListDealer.observe(requireActivity()) {
            it?.apply {
                result(IndexPagerData.PagerListData(), error = { msg ->
                    msg.toast()
                }, success = { data ->
                    mBinding.refreshLayout.finishLoadMore()
                    if (data.isFirstPage) {
                        mAdapter.putData(data.list)
                    } else {
                        mAdapter.addDataListToEnd(data.list)
                    }
                    mBinding.refreshLayout.setCanLoadMore(!data.isLastPage)
                })
            }
        }
    }

    override fun onDestroy() {
        mBinding.refreshLayout.unBindController()
        super.onDestroy()
    }
}