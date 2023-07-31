package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityMessageHistoryBinding
import net.knowfx.yaodonghui.entities.MessageData
import net.knowfx.yaodonghui.entities.MessageList
import net.knowfx.yaodonghui.ext.bindController
import net.knowfx.yaodonghui.ext.dismissLoadingDialog
import net.knowfx.yaodonghui.ext.getController
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setCanLoadMore
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.showLoadingDialog
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.unBindController
import net.knowfx.yaodonghui.ext.visible
import net.knowfx.yaodonghui.viewModels.MessageViewModel

class MessageHistoryActivity : BaseActivity() {
    private val mBinding = lazy { ActivityMessageHistoryBinding.inflate(layoutInflater) }
    private val mModel = lazy { ViewModelProvider(this)[MessageViewModel::class.java] }
    private val mAdapter = CommonListAdapter<MessageData>()
    private var mPage = 1

    override fun getContentView() = mBinding.value.root

    override fun initViewModel() {
        mModel.value.msgList.observe(this) {
            dismissLoadingDialog()
            it?.apply {
                result(MessageList(), error = { msg -> msg.toast() }, success = { data ->
                    if (data.isFirstPage) {
                        mAdapter.putData(data.list)
                        mBinding.value.emptyLayout.root.apply {
                            if (data.list.isNotEmpty()) {
                                gone()
                            } else {
                                visible()
                            }
                        }
                    } else {
                        mAdapter.addDataListToEnd(data.list)
                    }
                    mBinding.value.layoutList.refreshLayout.setCanLoadMore(!data.isLastPage)
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
        mBinding.value.layoutList.apply {
            recyclerView.layoutManager = LinearLayoutManager(this@MessageHistoryActivity)
            recyclerView.adapter = mAdapter
            refreshLayout.bindController(false) {
                mPage = it
                mModel.value.getMessageList(it)
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

    override fun onDestroy() {
        mBinding.value.layoutList.refreshLayout.unBindController()
        super.onDestroy()
    }
}