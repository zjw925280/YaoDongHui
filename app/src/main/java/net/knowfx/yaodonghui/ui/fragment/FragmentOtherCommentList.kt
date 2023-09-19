package net.knowfx.yaodonghui.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.databinding.LayoutSingleListBinding
import net.knowfx.yaodonghui.entities.MyOtherCommentData
import net.knowfx.yaodonghui.ext.bindController
import net.knowfx.yaodonghui.ext.getController
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setCanLoadMore
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.unBindController
import net.knowfx.yaodonghui.ui.activity.ArticleCommentContentActivity
import net.knowfx.yaodonghui.viewModels.MyPostViewModel

class FragmentOtherCommentList(private val status: Int) : Fragment() {
    private lateinit var mBinding: LayoutSingleListBinding
    private val mModel = lazy { ViewModelProvider(this)[MyPostViewModel::class.java] }
    private val mAdapter = CommonListAdapter<MyOtherCommentData.Data> { _, data, _ ->
        data as MyOtherCommentData.Data
        //跳转
        jumpToTarget(
            requireActivity(),
            ArticleCommentContentActivity::class.java,
            hashMapOf(Pair("id", data.id), Pair("model", data.model))
        )
    }

    private lateinit var mReceiver: MyReceiver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = LayoutSingleListBinding.inflate(inflater)
        registerReceiver()
        initObserver()
        initViews()
        return mBinding.root
    }

    private fun registerReceiver() {
        mReceiver = MyReceiver()
        val filter = IntentFilter()
        filter.addAction("COMMENT_DEL")
    }

    private fun initViews() {
        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = mAdapter
        }
        mBinding.refreshLayout.apply {
            bindController(true) {
                getDataList(it)
            }
            getController()?.refresh()
        }
    }

    private fun getDataList(page: Int) {
        mModel.value.getOtherCommentList(status, page)
    }

    private fun initObserver() {
        mModel.value.dataList.observe(this) {
            it?.apply {
                result(MyOtherCommentData(), error = { msg -> msg.toast() }, success = { data ->
                    if (data.isFirstPage) {
                        mAdapter.putData(dataList = data.list)
                    } else {
                        mAdapter.addDataListToEnd(dataList = data.list)
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

    inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, data: Intent?) {
            data?.apply {
                if (action == "COMMENT_DEL") {
                    val id = getIntExtra("id", 0)
                    if (id > 0) {
                        delData(id)
                    }
                }
            }
        }
    }

    private fun delData(id: Int) {
        val data = mAdapter.getDataList<MyOtherCommentData.Data>()
        for (i in 0 until data.size) {
            if (data[i].id == id) {
                mAdapter.delSingleData(i)
                return
            }
        }
    }


}