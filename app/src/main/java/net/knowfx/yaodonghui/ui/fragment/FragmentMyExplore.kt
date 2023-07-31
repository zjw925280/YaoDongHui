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
import net.knowfx.yaodonghui.entities.MyExploreData
import net.knowfx.yaodonghui.ext.bindController
import net.knowfx.yaodonghui.ext.getController
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setCanLoadMore
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ext.unBindController
import net.knowfx.yaodonghui.ext.visible
import net.knowfx.yaodonghui.ui.activity.ExploreCommentContentActivity
import net.knowfx.yaodonghui.viewModels.MyPostViewModel

class FragmentMyExplore(private val status: Int = -1) : Fragment() {
    private lateinit var mViewBinding: LayoutSingleListBinding
    private val mModel = lazy { ViewModelProvider(this)[MyPostViewModel::class.java] }
    private val mAdapter = CommonListAdapter<MyExploreData.Data> { _, data, _ ->
        data as MyExploreData.Data
        //点击item 跳转至曝光详情
        jumpToTarget(
            requireActivity(), ExploreCommentContentActivity::class.java, hashMapOf(
                Pair("id", data.id),
                Pair("isSelf", true),
                Pair("layoutType", ExploreCommentContentActivity.LAYOUT_TYPE_EXPLORE),
                Pair("cover", data.exposurefile)
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mViewBinding = LayoutSingleListBinding.inflate(inflater)
        initObservers()
        initViews()
        return mViewBinding.root
    }


    private fun initObservers() {
        mModel.value.dataList.observe(this) {
            mViewBinding.refreshLayout.finishRefresh()
            mViewBinding.refreshLayout.finishLoadMore()
            it?.apply {
                result(MyExploreData(), error = { msg -> msg.toast() }, success = { data ->
                    data.isFirstPage.trueLet {
                        if (data.list.isEmpty()) {
                            mViewBinding.layoutEmpty.root.visible()
                        } else {
                            mViewBinding.layoutEmpty.root.gone()
                            mAdapter.putData(dataList = data.list)
                        }
                    }.elseLet {
                        mAdapter.addDataListToEnd(dataList = data.list)
                    }
                    mViewBinding.refreshLayout.setCanLoadMore(!data.isLastPage)
                })
            }
        }
    }

    private fun initViews() {
        mViewBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mViewBinding.recyclerView.adapter = mAdapter
        mViewBinding.refreshLayout.apply {
            bindController(true) {
                mModel.value.getExploreLit(status, it)
            }
            getController()?.refresh()
        }
        mViewBinding.apply {
            layoutEmpty.noDataIv.setImageLevel(3)
            layoutEmpty.noDataTv.text = getString(R.string.string_no_data_post)
            layoutEmpty.noDataIv.setOnclick {
                layoutEmpty.root.gone()
                refreshLayout.getController()?.refresh()
            }
        }
    }

    override fun onDestroy() {
        mViewBinding.refreshLayout.unBindController()
        super.onDestroy()
    }
}