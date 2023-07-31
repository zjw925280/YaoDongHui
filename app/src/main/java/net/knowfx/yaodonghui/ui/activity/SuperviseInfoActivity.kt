package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivitySuperviseInfoBinding
import net.knowfx.yaodonghui.entities.SuperviseInfoData
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.into
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.visible
import net.knowfx.yaodonghui.utils.CommonMarginDecoration
import net.knowfx.yaodonghui.viewModels.SuperviseViewModel

class SuperviseInfoActivity : BaseActivity() {
    private lateinit var mBinding: ActivitySuperviseInfoBinding
    private val mAdapter = CommonListAdapter<SuperviseInfoData.FileData> { _, data, _ ->
        data as SuperviseInfoData.FileData
        //TODO 点击附件的操作
    }
    private val mModel = lazy { ViewModelProvider(this)[SuperviseViewModel::class.java] }
    private var mTargetId = 0
    private var mDealerId = 0
    override fun getContentView(): View {
        mBinding = ActivitySuperviseInfoBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initViewModel() {
        mModel.value.superviseContent.observe(this) {
            it?.apply {
                result(SuperviseInfoData(), error = { msg -> msg.toast() }, success = { data ->
                    setViews(data)
                })
            }
        }
    }


    private fun setViews(data: SuperviseInfoData) {
        mBinding.websiteContent.text = data.website
        mAdapter.putData(dataList = data.getFileList())
        mBinding.layoutImg.apply {
            contentIv.into(
                data.licensedlogo,
                start = { contentLoading.visible() },
                success = { contentLoading.gone() }
            )
        }
    }


    override fun setData(savedInstanceState: Bundle?) {
        mTargetId = bundle?.getInt("id") ?: 0
        mDealerId = bundle?.getInt("dealerId") ?: 0
        mBinding.filesRv.setHasFixedSize(true)
        mBinding.filesRv.isNestedScrollingEnabled = false
        mBinding.filesRv.layoutManager = object : GridLayoutManager(this, 4) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        val offset = resources.getDimensionPixelOffset(R.dimen.dp_9)
        mBinding.filesRv.addItemDecoration(CommonMarginDecoration(offset, offset, 4, false))
        mBinding.filesRv.adapter = mAdapter
        mModel.value.getSuperviseContent(mTargetId, mDealerId)
        mBinding.btnBack.setOnclick {
            finish()
        }
    }
}