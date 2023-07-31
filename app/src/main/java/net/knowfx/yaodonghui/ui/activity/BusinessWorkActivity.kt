package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.ActivityBusinessWorkBinding
import net.knowfx.yaodonghui.entities.PicData
import net.knowfx.yaodonghui.utils.*
import net.knowfx.yaodonghui.utils.imageSelector.SelectImageUtils
import com.ypx.imagepicker.bean.SelectMode
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.ext.dismissLoadingDialog
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.showLoadingDialog
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.viewModels.BusinessViewModel


class BusinessWorkActivity : BaseActivity() {
    private lateinit var mBinding: ActivityBusinessWorkBinding
    private val mModel = lazy { ViewModelProvider(this)[BusinessViewModel::class.java] }
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

    override fun getContentView(): View {
        mBinding = ActivityBusinessWorkBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initViewModel() {
        mModel.value.result.observe(this) {
            dismissLoadingDialog()
            it?.apply {
                if (!isSuccess) {
                    msg?.toast()
                } else {
                    "发起商务合作成功".toast()
                    finish()
                }
            }
        }
    }

    override fun setData(savedInstanceState: Bundle?) {
        initViews()
        super.setData(savedInstanceState)
    }

    private fun updatePic(list: ArrayList<PicData>) {
        mAdapter.delSingleData(mAdapter.getDataList<PicData>().size - 1)
        mPicList.addAll(list)
        mAdapter.addDataListToEnd(list)
        if (mPicList.size < 12) {
            mAdapter.addDataListToEnd(arrayListOf(PicData()))
        }
    }


    private fun initViews() {
        mBinding.picRv.setHasFixedSize(true)
        mBinding.picRv.isNestedScrollingEnabled = false
        val offset = resources.getDimensionPixelOffset(R.dimen.dp_5)
        mBinding.picRv.addItemDecoration(CommonMarginDecoration(offset, offset, 3, false))
        mBinding.picRv.layoutManager = object : GridLayoutManager(this, 3) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        mBinding.picRv.adapter = mAdapter
        mAdapter.putData(mPicList)
        mAdapter.addDataListToEnd(arrayListOf(PicData()))
        mBinding.btnConfirm.setOnclick {
            showLoadingDialog()
            checkAndSubmit()
        }
        mBinding.btnBack.setOnclick {
            finish()
        }
    }

    private fun checkAndSubmit() {
        val name = mBinding.edtName.text.toString().trim()
        val mail = mBinding.edtMailBox.text.toString().trim()
        val purpose = mBinding.edtReason.text.toString().trim()
        if (name.isEmpty()) {
            "请输入姓名".toast()
            return
        }

        if (mail.isEmpty()) {
            "请输入邮箱".toast()
            return
        }

        if (purpose.isEmpty()) {
            "请输入合作目的".toast()
            return
        }
        mModel.value.submit(name, mail, purpose, mPicList)
    }
}