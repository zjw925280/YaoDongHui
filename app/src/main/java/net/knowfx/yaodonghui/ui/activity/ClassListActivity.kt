package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.databinding.ActivityClassListBinding
import net.knowfx.yaodonghui.entities.ClassListData
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.entities.ShareData
import net.knowfx.yaodonghui.ext.bindController
import net.knowfx.yaodonghui.ext.createShareUrl
import net.knowfx.yaodonghui.ext.dismissLoadingDialog
import net.knowfx.yaodonghui.ext.getArticleShareBitmap
import net.knowfx.yaodonghui.ext.getController
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setCanLoadMore
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.share
import net.knowfx.yaodonghui.ext.showLoadingDialog
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.utils.CommonMarginDecoration
import net.knowfx.yaodonghui.viewModels.ClassViewModel

class ClassListActivity : BaseActivity() {
    private lateinit var mBinding: ActivityClassListBinding
    private val mModel = lazy { ViewModelProvider(this)[ClassViewModel::class.java] }
    private val mAdapter = CommonListAdapter<ClassListData.Data> { view, data, position ->
        data as ClassListData.Data
        when (view.id) {
            R.id.btnShare -> {
                //分享
                val sData = ShareData(
                    title = data.title,
                    content = getString(R.string.string_broker_share_content),
                    url = hashMapOf<String, Any>(
                        Pair("id", data.id),
                    ).createShareUrl("JGXT"),
                    thumbBitmap = getArticleShareBitmap("JGXT")
                )
                share(activity = this, data = sData, activeSavePic = false)
            }

            R.id.btnComment -> {
                //评论
                jumpToTarget(
                    this,
                    ClassContentActivity::class.java,
                    hashMapOf(Pair("id", data.id), Pair("autoScroll", true))
                )
            }

            R.id.btnFocus -> {
                //关注
                showLoadingDialog()
                mFocusPosition = position
                commonViewModel.value.focus("JGXT", data.id, data.follow)
            }

            else -> {
                //点击进入详情
                jumpToTarget(
                    this,
                    ClassContentActivity::class.java,
                    hashMapOf(Pair("id", data.id), Pair("autoScroll", false))
                )
            }
        }
    }
    private var mFocusPosition = -1

    override fun isUseFullScreenMode() = true
    override fun getContentView(): View {
        mBinding = ActivityClassListBinding.inflate(layoutInflater)
        mBinding.btnBack.setOnclick {
            finish()
        }
        mBinding.btnSearch.setOnclick {
            jumpToTarget(this, ClassSearchActivity::class.java, hashMapOf(Pair("flag", "JGXT")))
        }
        return mBinding.root
    }

    override fun initViewModel() {
        mModel.value.classList.observe(this) {
            it?.apply {
                result(ClassListData(), error = { msg -> msg.toast() }, success = { data ->
                    data.isFirstPage.trueLet {
                        mAdapter.putData(dataList = data.list)
                    }.elseLet {
                        mAdapter.addDataListToEnd(dataList = data.list)
                    }
                    mBinding.refreshLayout.setCanLoadMore(!data.isLastPage)
                })
            }
        }

        commonViewModel.value.focusResult.observe(this) {
            dismissLoadingDialog()
            it?.apply {
                if (isSuccess) {
                    data?.apply {
                        val data = mAdapter.getDataList<ClassListData.Data>()[mFocusPosition]
                        data.follow = this
                        mAdapter.updateData(data, mFocusPosition)
                        mFocusPosition = -1
                    }
                }
            }
        }
    }

    override fun setData(savedInstanceState: Bundle?) {
        mBinding.listRv.layoutManager = LinearLayoutManager(this)
        mBinding.listRv.addItemDecoration(CommonMarginDecoration(0, 0, 1, false))
        mBinding.listRv.adapter = mAdapter
        mBinding.refreshLayout.bindController(true) {
            mModel.value.getClassList(it)
        }
        mBinding.refreshLayout.getController()?.refresh()
        super.setData(savedInstanceState)
    }
}