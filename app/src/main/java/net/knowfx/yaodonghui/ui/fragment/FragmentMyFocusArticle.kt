package net.knowfx.yaodonghui.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseSelectFragment
import net.knowfx.yaodonghui.databinding.LayoutSingleListBinding
import net.knowfx.yaodonghui.entities.FocusArticleData
import net.knowfx.yaodonghui.ext.bindController
import net.knowfx.yaodonghui.ext.getController
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setCanLoadMore
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.unBindController
import net.knowfx.yaodonghui.ext.visible
import net.knowfx.yaodonghui.ui.activity.ArticleContentActivity
import net.knowfx.yaodonghui.ui.activity.ClassContentActivity
import net.knowfx.yaodonghui.ui.activity.DrawContentActivity
import net.knowfx.yaodonghui.ui.activity.MyFocusActivity
import net.knowfx.yaodonghui.viewModels.FocusViewModel

class FragmentMyFocusArticle : BaseSelectFragment() {
    private val mBinding = lazy { LayoutSingleListBinding.inflate(layoutInflater) }
    private val mModel = lazy { ViewModelProvider(this)[FocusViewModel::class.java] }
    private val mDataList = ArrayList<FocusArticleData.ItemData>()
    private val mAdapter = CommonListAdapter<FocusArticleData.ItemData> { view, data, position ->
        data as FocusArticleData.ItemData
        if (mIsSelectMode) {
            if (mSelectedList.contains(data.attentionId)) {
                mSelectedList.remove(data.attentionId)
            } else {
                mSelectedList.add(data.attentionId)
            }
            commitCount()
        } else {
            when (data.model) {
                "JGXT" -> {
                    jumpToTarget(
                        requireActivity(),
                        ClassContentActivity::class.java,
                        hashMapOf(Pair("id", data.modelId))
                    )
                }

                "HHQ" -> {
                    jumpToTarget(
                        requireActivity(),
                        DrawContentActivity::class.java,
                        hashMapOf(Pair("id", data.modelId), Pair("code", data.model))
                    )
                }

                else -> {
                    jumpToTarget(
                        requireActivity(),
                        ArticleContentActivity::class.java,
                        hashMapOf(Pair("id", data.modelId), Pair("flag", data.model))
                    )
                }
            }
        }
    }
    private val mSelectedList = ArrayList<Int>()
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initObservers()
        initViews()
        return mBinding.value.root
    }

    private fun commitCount() {
        (requireActivity() as MyFocusActivity).updateCount(
            mSelectedList.size,
            mSelectedList.size == mDataList.size
        )
    }

    private fun switchMode() {
        mDataList.forEach {
            it.isInSelectMode = mIsSelectMode
            if (!mIsSelectMode) {
                it.isSelected = false
            }
        }
        mAdapter.putData(mDataList)
    }

    private fun initObservers() {
        mModel.value.articleList.observe(this) {
            it?.apply {
                result(FocusArticleData(), error = { msg -> msg.toast() }, success = { data ->
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
            recyclerView.adapter = mAdapter
            refreshLayout.bindController(true) { page ->
                //请求接口
                mModel.value.getFocusArticle(page)
            }
            refreshLayout.getController()?.refresh()
            mBinding.value.layoutEmpty.noDataIv.setImageLevel(4)
            mBinding.value.layoutEmpty.noDataTv.text = getString(R.string.string_no_data_focus)
            mBinding.value.layoutEmpty.noDataIv.setOnclick {
                mBinding.value.layoutEmpty.root.gone()
                mBinding.value.refreshLayout.getController()?.refresh()
            }
        }
    }

    override fun onDestroy() {
        mBinding.value.refreshLayout.unBindController()
        super.onDestroy()
    }
}