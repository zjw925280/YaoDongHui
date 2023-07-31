package net.knowfx.yaodonghui.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityRegisterBinding
import net.knowfx.yaodonghui.ext.checkPwd
import net.knowfx.yaodonghui.ext.dismissLoadingDialog
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setMultipleClick
import net.knowfx.yaodonghui.ext.showLoadingDialog
import net.knowfx.yaodonghui.ext.startCountDownForGetCode
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.utils.ToastUtils
import net.knowfx.yaodonghui.viewModels.LoginRegisterViewModel


class RegisterActivity : BaseActivity() {
    private lateinit var mBinding: ActivityRegisterBinding
    private lateinit var mViewModel: LoginRegisterViewModel

    override fun getContentView(): View {
        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        initViews()
        return mBinding.root
    }

    override fun initViewModel() {
        super.initViewModel()
        mViewModel = ViewModelProvider(this)[LoginRegisterViewModel::class.java]
        mViewModel.phoneCodeResult.observe(this) {
            //请求手机验证码成功，开始倒计时
            dismissLoadingDialog()
            it?.apply {
                result(String(), error = { msg ->
                    msg.toast()
                }, success = {
                    mBinding.layoutPhoneCheck.btnGetCode.startCountDownForGetCode()
                })
            } ?: {
                "获取手机验证码失败，请稍后重试".toast()
            }
        }

        mViewModel.registerResult.observe(this) {
            dismissLoadingDialog()
            it?.apply {
                it.result(String(), error = {
                    "注册失败，请稍后重试".toast()
                }, success = {
                    registerFinish()
                })
            }
        }
    }

    private fun initViews() {
        //设置用户协议和隐私策略的可点击逻辑
        val str = getString(R.string.string_login_contract)
        val spannable = SpannableString(str)
        val userContractStart = str.indexOf("《")
        val userContractEnd = str.indexOf("》") + 1
        val privacyStart = str.lastIndexOf("《")
        val privacyEnd = str.lastIndexOf("》") + 1
        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.color_9C67E6))
        val userContractSpannable = object : ClickableSpan() {
            override fun onClick(p0: View) {
                //打开用户协议
                jumpToTarget(
                    this@RegisterActivity,
                    ContractActivity::class.java,
                    hashMapOf(Pair("isPri", false)))
            }

        }
        val privacySpannable = object : ClickableSpan() {
            override fun onClick(p0: View) {
                //打开隐私策略
                jumpToTarget(
                    this@RegisterActivity,
                    ContractActivity::class.java,
                    hashMapOf(Pair("isPri", true)))
            }
        }
        spannable.setSpan(
            userContractSpannable,
            userContractStart,
            userContractEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            colorSpan,
            userContractStart,
            userContractEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            privacySpannable,
            privacyStart,
            privacyEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(colorSpan, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mBinding.tvContract.movementMethod = LinkMovementMethod.getInstance()
        mBinding.tvContract.highlightColor = Color.TRANSPARENT
        mBinding.tvContract.text = spannable
        addListeners()
    }

    private fun addListeners() {
        setMultipleClick(
            mBinding.btnBack,
            mBinding.layoutPhoneCheck.btnGetCode,
            mBinding.btnRegister,
            mBinding.radioContract
        ) {
            when (it) {
                mBinding.btnBack -> {
                    finish()
                }

                mBinding.layoutPhoneCheck.btnGetCode -> {
                    val result = checkPhone()
                    if (!result.first) {
                        result.second.toast()
                        return@setMultipleClick
                    }
                    showLoadingDialog()
                    mViewModel.requestPhoneCode(
                        mBinding.layoutPhoneCheck.edtPhone.text.toString().trim()
                    )
                }

                mBinding.btnRegister -> {
                    //点击注册按钮
                    checkParams()
                }

                mBinding.radioContract -> {
                    mBinding.radioContract.isSelected = !mBinding.radioContract.isSelected
                }

                else -> {}
            }
        }
    }

    private fun checkParams() {
        val phone = mBinding.layoutPhoneCheck.edtPhone.text.toString()
        val code = mBinding.layoutPhoneCheck.edtCode.text.toString()
        val pwd = mBinding.layoutPwdCheck.edtPwd.text.toString()
        val pwdAgain = mBinding.layoutPwdCheck.edtPwdAgain.text.toString()
        if (phone.isEmpty()) {
            "请输入手机号码".toast()
            return
        }
        if (phone.length < 11) {
            "请输入正确的手机号码".toast()
            return
        }
        if (code.isEmpty()) {
            "请输入手机验证码".toast()
            return
        }
        if (pwd.isEmpty()) {
            "请输入密码".toast()
            return
        }
        if (!pwd.checkPwd()){
            "密码为6~18位字母数字组合".toast()
            return
        }
        if (pwdAgain.isEmpty()) {
            "请输入确认密码".toast()
            return
        }
        if (!pwdAgain.checkPwd()){
            "密码为6~18位字母数字组合".toast()
            return
        }
        if (pwd != pwdAgain) {
            "两次输入密码不一致，请确认密码输入".toast()
            return
        }
        if (!mBinding.radioContract.isSelected) {
            "请阅读并同意用户协议与隐私政策".toast()
            return
        }
        doRegister(phone, pwd, pwdAgain, code)
    }

    private fun doRegister(phone: String, pwd: String, rePwd: String, code: String) {
        //请求接口注册
        showLoadingDialog()
        mViewModel.registerAccount(phone, pwd, rePwd, code)
    }

    private fun registerFinish() {
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun checkPhone(): Pair<Boolean, String> {
        val phone = mBinding.layoutPhoneCheck.edtPhone.text.toString().trim()
        return if (phone.isEmpty()) {
            Pair(false, "请输入手机号码")
        } else if (phone.length < 11) {
            Pair(false, "请输入正确的手机号码")
        } else {
            Pair(true, phone)
        }
    }

}