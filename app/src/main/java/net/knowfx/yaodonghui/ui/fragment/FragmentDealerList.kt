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
import net.knowfx.yaodonghui.entities.MyDealerCommentData
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
import net.knowfx.yaodonghui.ui.activity.BrokerContentActivity
import net.knowfx.yaodonghui.viewModels.MyPostViewModel

class FragmentDealerList(private val status: Int) : Fragment() {
    private val mBinding = lazy { LayoutSingleListBinding.inflate(layoutInflater) }
    private val mAdapter = CommonListAdapter<MyDealerCommentData.Data> { _, data, _ ->
        data as MyDealerCommentData.Data
        jumpToTarget(
            requireActivity(),
            BrokerContentActivity::class.java,
            hashMapOf(Pair("brokerId", data.dealerId))
        )
    }
    private val mModel = lazy { ViewModelProvider(this)[MyPostViewModel::class.java] }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initObserver()
        initViews()
        return mBinding.value.root
    }

    private fun initObserver() {
        mModel.value.dataList.observe(this) {
            it?.apply {
                result(MyDealerCommentData(), error = { msg -> msg.toast() }, success = { data ->
                    if (data.isFirstPage) {
                        if (data.list.isEmpty()) {
                            mBinding.value.layoutEmpty.root.visible()
                        } else {
                            mBinding.value.layoutEmpty.root.gone()
                            mAdapter.putData(dataList = data.list)
                        }
                    } else {
                        mAdapter.addDataListToEnd(dataList = data.list)
                    }
                    mBinding.value.refreshLayout.setCanLoadMore(!data.isLastPage)
                })
            }
        }
    }

    private fun initViews() {
        mBinding.value.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = mAdapter
        }
        mBinding.value.refreshLayout.apply {
            bindController(true) {
                getDataList(it)
            }
            getController()?.refresh()
        }
        mBinding.value.layoutEmpty.noDataIv.setImageLevel(3)
        mBinding.value.layoutEmpty.noDataTv.text = getString(R.string.string_no_data_post)
        mBinding.value.layoutEmpty.noDataIv.setOnclick {
            mBinding.value.layoutEmpty.root.gone()
            mBinding.value.refreshLayout.getController()?.refresh()
        }
    }

    private fun getDataList(page: Int) {
        mModel.value.getDealerCommentList(status = status, page = page)
    }

    override fun onDestroy() {
        mBinding.value.refreshLayout.unBindController()
        super.onDestroy()
    }

}