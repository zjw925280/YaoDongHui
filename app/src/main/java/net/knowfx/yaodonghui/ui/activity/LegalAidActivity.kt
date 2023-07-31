package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.ActivityLegalAidBinding
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.ext.getUserData
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.viewModels.DealerViewModel

class LegalAidActivity : BaseActivity() {
    private lateinit var mBinding: ActivityLegalAidBinding
    private val mModel = lazy { ViewModelProvider(this)[DealerViewModel::class.java] }
    private var mBrokerId = 0
    override fun getContentView(): View {
        mBinding = ActivityLegalAidBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initViewModel() {
        mModel.value.postResult.observe(this) {
            it?.apply {
                if (isSuccess) {
                    "法律援助提交成功".toast()
                    finish()
                }
            }
        }
    }


    override fun setData(savedInstanceState: Bundle?) {
        mBrokerId = bundle?.getInt("id") ?: 0
        getUserData()?.apply {
            mBinding.nameTv.text = surname + name
            mBinding.idCodeTv.text = idcard
            mBinding.phoneTv.text = phone
        }
        addListeners()
    }

    private fun addListeners() {
        mBinding.btnBack.setOnclick { finish() }
        mBinding.btnSubmit.setOnclick { checkAndSubmit() }
    }

    private fun checkAndSubmit() {
        val mail = mBinding.mailEdt.text.toString().trim()
        val account = mBinding.accountEdt.text.toString().trim()
        if (mail.isEmpty()) {
            "请输入您的邮箱".toast()
            return
        }
        if (account.isEmpty()) {
            "请输入您的交易账号".toast()
            return
        }
        mModel.value.postHelp(mBrokerId, account, mail)
    }
}