package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityPushHistoryBinding
import net.knowfx.yaodonghui.entities.PushData
import net.knowfx.yaodonghui.entities.PushList
import net.knowfx.yaodonghui.ext.bindController
import net.knowfx.yaodonghui.ext.getController
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.jumpFromPush
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setCanLoadMore
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.showLoadingDialog
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.visible
import net.knowfx.yaodonghui.viewModels.MessageViewModel

class PushHistoryActivity : BaseActivity() {
    private val mBinding = lazy { ActivityPushHistoryBinding.inflate(layoutInflater) }
    private val mModel = lazy { ViewModelProvider(this)[MessageViewModel::class.java] }
    private val mAdapter = CommonListAdapter<PushData>{ _, data, _ ->
        data as PushData
        jumpFromPush(data.model, data.buinessId)
    }
    private var mPage = 1

    override fun getContentView() = mBinding.value.root

    override fun initViewModel() {
        mModel.value.pushList.observe(this) {
            it?.apply {
                result(ArrayList<PushList>(), error = { msg -> msg.toast() }, success = { list ->
                    if (list.isNotEmpty()) {
                        val dataList = formatList(list)
                        if (mPage == 1) {
                            mAdapter.putData(dataList)
                            mBinding.value.emptyLayout.root.apply {
                                if (dataList.isNotEmpty()) {
                                    gone()
                                } else {
                                    visible()
                                }
                            }
                        } else {
                            mAdapter.addDataListToEnd(dataList)
                        }
                    }
                    mBinding.value.layoutList.refreshLayout.setCanLoadMore(list.isNotEmpty())
                })
            }
        }
    }

    override fun setData(savedInstanceState: Bundle?) {
        super.setData(savedInstanceState)
        initViews()
        addListeners()
    }

    private fun initViews() {
        mBinding.value.titleTv.text = bundle?.getString("title") ?: "推送历史"
        mBinding.value.layoutList.apply {
            recyclerView.layoutManager = LinearLayoutManager(this@PushHistoryActivity)
            recyclerView.adapter = mAdapter
            refreshLayout.bindController(false) {
                mPage = it
                mModel.value.getPushList(it)
            }
            refreshLayout.getController()?.refresh()
        }
        mBinding.value.emptyLayout.apply {
            noDataIv.setImageLevel(0)
            noDataTv.text = getString(R.string.string_no_data_message)
            noDataIv.setOnclick {
                showLoadingDialog()
                mBinding.value.layoutList.refreshLayout.getController()?.refresh()
            }
        }
    }

    private fun addListeners() {
        mBinding.value.btnBack.setOnclick {
            finish()
        }
    }

    private fun formatList(list: ArrayList<PushList>): ArrayList<PushData> {
        val result = ArrayList<PushData>()
        var date = ""
        list.forEach {
            date = it.fomTime
            it.propeHistoryInfoDTOList.forEach { push ->
                if (date.isNotEmpty()) {
                    push.isShowDate = true
                    push.date = date
                    date = ""
                } else {
                    push.isShowDate = false
                }
                result.add(push)
            }
        }
        return result
    }
}