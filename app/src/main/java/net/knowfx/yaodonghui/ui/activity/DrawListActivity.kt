package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityDrawListBinding
import net.knowfx.yaodonghui.entities.DrawCircleListData
import net.knowfx.yaodonghui.ext.bindController
import net.knowfx.yaodonghui.ext.getController
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setCanLoadMore
import net.knowfx.yaodonghui.ext.setMultipleClick
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.unBindController
import net.knowfx.yaodonghui.utils.CommonMarginDecoration
import net.knowfx.yaodonghui.viewModels.ArticleViewModel

class DrawListActivity : BaseActivity() {
    private lateinit var mBinding: ActivityDrawListBinding
    private val mViewModel = lazy { ViewModelProvider(this)[ArticleViewModel::class.java] }
    private var mFlag = ""
    private val mAdapter = CommonListAdapter<DrawCircleListData.Data> { _, data, _ ->
        data as DrawCircleListData.Data
        //点击跳转
        jumpToTarget(
            this,
            DrawContentActivity::class.java,
            hashMapOf(Pair("id", data.id), Pair("code", mFlag), Pair("cover", data.coverPicture))
        )
    }

    override fun getContentView(): View {
        mBinding = ActivityDrawListBinding.inflate(layoutInflater)
        initObservers()
        return mBinding.root
    }

    private fun initObservers() {
        mViewModel.value.articleList.observe(this) {
            mBinding.refresh.finishLoadMore()
            mBinding.refresh.finishRefresh()
            it?.apply {
                result(DrawCircleListData(), error = { msg ->
                    msg.toast()
                }, success = { data ->
                    if (data.isFirstPage) {
                        mAdapter.putData(data.list)
                    } else {
                        mAdapter.addDataListToEnd(dataList = data.list)
                    }
                    mBinding.refresh.setCanLoadMore(!data.isLastPage)
                })
            }
        }
    }


    override fun setData(savedInstanceState: Bundle?) {
        mFlag = bundle?.getString("code") ?: ""
        mBinding.listRv.layoutManager = LinearLayoutManager(this)
        mBinding.listRv.addItemDecoration(CommonMarginDecoration(0, 0, 1, false))
        mBinding.listRv.adapter = mAdapter
        mBinding.refresh.bindController(true) {
            mViewModel.value.getDrawList(page = it)
        }
        setListeners()
        mBinding.refresh.getController()?.refresh()
        super.setData(savedInstanceState)
    }

    override fun onDestroy() {
        mBinding.refresh.unBindController()
        super.onDestroy()
    }

    private fun setListeners() {
        setMultipleClick(mBinding.btnBack, mBinding.btnSearch) {
            when (it) {
                mBinding.btnBack -> {
                    this.finish()
                }

                else -> {
                    //跳转至画汇圈的搜索界面
                    jumpToTarget(this, DrawSearchActivity::class.java, hashMapOf(Pair("flag", mFlag)))
                }
            }
        }
    }
}