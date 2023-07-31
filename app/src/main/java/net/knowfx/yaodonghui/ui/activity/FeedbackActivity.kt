package net.knowfx.yaodonghui.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.ypx.imagepicker.bean.SelectMode
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityFeedbackBinding
import net.knowfx.yaodonghui.entities.PicData
import net.knowfx.yaodonghui.ext.dismissLoadingDialog
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.showLoadingDialog
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.utils.CommonMarginDecoration
import net.knowfx.yaodonghui.utils.imageSelector.SelectImageUtils

class FeedbackActivity : BaseActivity() {
    private val mBinding = lazy { ActivityFeedbackBinding.inflate(layoutInflater) }
    private val mPicList = ArrayList<PicData>()
    private val mAdapter = CommonListAdapter<PicData> { _, data, _ ->
        data as PicData
        if (data.picServicePath.isEmpty() && data.picLocalPath.isEmpty()) {
            SelectImageUtils.pickOnly(this, true, 12, SelectMode.MODE_MULTI) {
                it?.apply {
                    if (isNotEmpty()) {
                        updatePic(this)
                    }
                }
            }
        }
    }

    override fun getContentView() = mBinding.value.root

    override fun initViewModel() {
        super.initViewModel()
        commonViewModel.value.feedbackResult.observe(this) {
            dismissLoadingDialog()
            it?.apply {
                if (!isSuccess) {
                    msg?.toast()
                } else {
                    finish()
                }
            }
        }
    }

    override fun setData(savedInstanceState: Bundle?) {
        initViews()
        super.setData(savedInstanceState)
    }

    private fun initViews() {
        mBinding.value.apply {
            picRv.setHasFixedSize(true)
            picRv.isNestedScrollingEnabled = false
            val offset = resources.getDimensionPixelOffset(R.dimen.dp_5)
            picRv.addItemDecoration(CommonMarginDecoration(offset, offset, 3, false))
            picRv.layoutManager = object : GridLayoutManager(this@FeedbackActivity, 3) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            picRv.adapter = mAdapter
            mAdapter.putData(mPicList)
            mAdapter.addDataListToEnd(arrayListOf(PicData()))
            btnSubmit.setOnclick {
                checkAndSubmit()
            }
            btnBack.setOnclick {
                finish()
            }
        }
    }

    private fun checkAndSubmit() {
        val theme = mBinding.value.edtTheme.text.toString().trim()
        val content = mBinding.value.edtContent.text.toString().trim()
        val phone = mBinding.value.edtPhone.text.toString().trim()
        if (theme.isEmpty()) {
            "请填写反馈主题".toast()
            return
        } else
            if (content.isEmpty()) {
                "请填写反馈内容".toast()
                return
            } else
                if (phone.isEmpty()) {
                    "请填写联系方式".toast()
                    return
                }
        showLoadingDialog()
        commonViewModel.value.feedback(theme, content, phone, mPicList)
    }

    private fun updatePic(list: ArrayList<PicData>) {
        mAdapter.delSingleData(mAdapter.getDataList<PicData>().size - 1)
        mPicList.addAll(list)
        mAdapter.addDataListToEnd(list)
        if (mPicList.size < 12){
            mAdapter.addDataListToEnd(arrayListOf(PicData()))
        }
    }
}