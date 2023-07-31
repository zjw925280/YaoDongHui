package net.knowfx.yaodonghui.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.databinding.LayoutSingleListBinding
import net.knowfx.yaodonghui.entities.ExploreListData
import net.knowfx.yaodonghui.ext.bindController
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setCanLoadMore
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ext.unBindController
import net.knowfx.yaodonghui.ui.activity.ExploreCommentContentActivity
import net.knowfx.yaodonghui.utils.ExploreTypeEnum
import net.knowfx.yaodonghui.utils.MyApplication
import net.knowfx.yaodonghui.viewModels.ExploreViewModel

class FragmentExploreList(private val type: ExploreTypeEnum = ExploreTypeEnum.TYPE_EXPLORE_ALL) :
    Fragment() {
    private lateinit var mViewBinding: LayoutSingleListBinding
    private val mModel = lazy { ViewModelProvider(this)[ExploreViewModel::class.java] }
    private val mAdapter = CommonListAdapter<ExploreListData.Data> { view, data, position ->
        data as ExploreListData.Data
        //点击item 跳转至曝光详情
        jumpToTarget(
            requireActivity(), ExploreCommentContentActivity::class.java, hashMapOf(
                Pair("isSelf", false),
                Pair("id", data.id),
                Pair("layoutType", ExploreCommentContentActivity.LAYOUT_TYPE_EXPLORE),
                Pair("cover", data.logo)
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mViewBinding = LayoutSingleListBinding.inflate(inflater)
        initViews()
        initObservers()
        return mViewBinding.root
    }

    private fun initViews() {
        mViewBinding.refreshLayout.setEnableRefresh(true)
        mViewBinding.refreshLayout.setEnableLoadMore(false)
        mViewBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mViewBinding.recyclerView.adapter = mAdapter
        mViewBinding.refreshLayout.autoRefresh()
        mViewBinding.refreshLayout.bindController(true) {
            mModel.value.getExploreList(type.value, it, MyApplication.COMMON_PAGE_SIZE)
        }
    }

    private fun initObservers() {
        mModel.value.exploreList.observe(this) {
            mViewBinding.refreshLayout.finishRefresh()
            mViewBinding.refreshLayout.finishLoadMore()
            it?.apply {
                result(ExploreListData(), error = { msg -> msg.toast() }, success = { data ->
                    data.isFirstPage.trueLet {
                        mAdapter.putData(dataList = data.list)
                    }.elseLet {
                        mAdapter.addDataListToEnd(dataList = data.list)
                    }
                    mViewBinding.refreshLayout.setCanLoadMore(!data.isLastPage)
                })
            }
        }
    }

    override fun onDestroy() {
        mViewBinding.refreshLayout.unBindController()
        super.onDestroy()
    }
}