package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.databinding.ActivityArticleListBinding
import net.knowfx.yaodonghui.entities.ArticleListData
import net.knowfx.yaodonghui.ext.setMultipleClick
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.ext.bindController
import net.knowfx.yaodonghui.ext.getController
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setCanLoadMore
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.utils.CommonMarginDecoration
import net.knowfx.yaodonghui.viewModels.ArticleViewModel

class ArticleListActivity : BaseActivity() {
    private lateinit var mBinding: ActivityArticleListBinding
    private val mModel = lazy { ViewModelProvider(this)[ArticleViewModel::class.java] }
    private var mFlag = ""
    private val mAdapter = CommonListAdapter<ArticleListData.ListData> { _, data, _ ->
        data as ArticleListData.ListData
        //跳转到详情页面
        jumpToTarget(
            this,
            ArticleContentActivity::class.java,
            hashMapOf(Pair("id", data.id), Pair("flag", data.model.ifEmpty { mFlag }))
        )
    }

    override fun getContentView(): View {
        mBinding = ActivityArticleListBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initViewModel() {
        super.initViewModel()
        mModel.value.articleList.observe(this) {
            it?.apply {
                result(ArticleListData(), error = { msg -> msg.toast() }, success = { data ->
                    if (data.isFirstPage) {
                        mAdapter.putData(data.list)
                    } else {
                        mAdapter.addDataListToEnd(data.list)
                    }
                    mBinding.refresh.setCanLoadMore(!data.isLastPage)
                })
            }
        }

    }

    override fun setData(savedInstanceState: Bundle?) {
        super.setData(savedInstanceState)
        mFlag = bundle?.getString("code") ?: ""
        mBinding.titleTv.text = getTitleStr()
        mBinding.refresh.bindController(refreshFlag = true) {
            requestList(it)
        }
        mBinding.listRv.layoutManager = LinearLayoutManager(this)
        mBinding.listRv.addItemDecoration(CommonMarginDecoration(0, 0, 1, false))
        mBinding.listRv.adapter = mAdapter
        setListeners()
        mBinding.refresh.getController()?.refresh()
    }

    private fun setListeners() {
        setMultipleClick(mBinding.btnBack, mBinding.btnSearch) {
            when (it) {
                mBinding.btnBack -> {
                    this.finish()
                }

                else -> {
                    //打开搜索界面
                    jumpToTarget(this, ArticleSearchActivity::class.java, hashMapOf(Pair("flag", mFlag)))
                }
            }
        }
    }

    private fun requestList(page: Int) {
        //请求列表的接口
        when (mFlag) {
            "ZXH" -> {//真相汇
                mModel.value.getTruthList(page)
            }

            "HQST" -> {//汇圈神探
                mModel.value.getSherlockList(page)
            }

            "DSPH" -> {//毒蛇评汇
                mModel.value.getSnakeList(page)
            }

            else -> {}
        }
    }

    private fun getTitleStr(): String{
        return when (mFlag) {
            "ZXH" -> {//真相汇
                "真相汇"
            }

            "HQST" -> {//汇圈神探
                "汇圈神探"
            }

            "DSPH" -> {//毒蛇评汇
                "毒舌评汇"
            }

            else -> ""
        }
    }
}