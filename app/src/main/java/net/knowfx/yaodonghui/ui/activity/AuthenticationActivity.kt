package net.knowfx.yaodonghui.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import net.knowfx.yaodonghui.databinding.ActivityAuthenticationBinding
import net.knowfx.yaodonghui.ext.setMultipleClick
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.ext.dismissLoadingDialog
import net.knowfx.yaodonghui.ext.getUserData
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.saveUserData
import net.knowfx.yaodonghui.ext.showLoadingDialog
import net.knowfx.yaodonghui.ext.startCountDownForGetCode
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.utils.CheckIDCardRule
import net.knowfx.yaodonghui.utils.ToastUtils
import net.knowfx.yaodonghui.viewModels.AuthenticationViewModel

/**
 * 实名认证的界面
 */
class AuthenticationActivity : BaseActivity() {
    private lateinit var mBinding: ActivityAuthenticationBinding
    private val mModel = lazy { ViewModelProvider(this)[AuthenticationViewModel::class.java] }
    override fun getContentView(): View {
        mBinding = ActivityAuthenticationBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initViewModel() {
        super.initViewModel()
        mModel.value.phoneCodeResult.observe(this) {
            //请求手机验证码成功，开始倒计时
            dismissLoadingDialog()
            it?.apply {
                result(String(), error = { msg ->
                    msg.toast()
                }, success = {
                    mBinding.phoneCodeSend.startCountDownForGetCode()
                })
            } ?: {
                "获取手机验证码失败，请稍后重试".toast()
            }
        }
        mModel.value.submitResult.observe(this) {
            it?.apply {
                if (isSuccess) {
                    val data = Intent()
                    data.putExtra("isSuccess", true)
                    setResult(Activity.RESULT_OK, data)
                    val userData = getUserData()
                    userData?.apply {
                        userData.iscert = 1
                        userData.idcard = mBinding.idCodeEdt.text.toString().trim()
                        userData.surname = mBinding.firstNameEdt.text.toString().trim()
                        userData.name = mBinding.lastNameEdt.text.toString().trim()
                        saveUserData()
                    }
                    "认证成功！"
                } else {
                    val data = Intent()
                    data.putExtra("isSuccess", false)
                    setResult(Activity.RESULT_OK, data)
                    "实名认证失败，请稍后重试"
                }.toast()
                finish()
            }
        }
    }

    override fun setData(savedInstanceState: Bundle?) {
        setMultipleClick(mBinding.btnBack, mBinding.phoneCodeSend, mBinding.btnSubmit) {
            when (it) {
                mBinding.btnBack -> {
                    this.finish()
                }

                mBinding.phoneCodeSend -> {
                    //请求服务器获取手机验证码
                    val phone = getUserData()?.phone
                    if (phone.isNullOrEmpty()) {
                        "获取手机验证码失败，请稍后重试".toast()
                        return@setMultipleClick
                    }
                    showLoadingDialog()
                    mModel.value.requestPhoneCode(phone)
                }

                else -> {
                    //请求接口提交数据
                    val result = checkContentsAndSubmit()
                    if (result.isNotEmpty()) {
                        result.toast()
                    }
                }
            }
        }
    }


    private fun checkContentsAndSubmit(): String {
        val firstName = mBinding.firstNameEdt.text.toString().trim()
        if (firstName.isEmpty()) {
            return "请输入姓氏"
        }

        val lastName = mBinding.lastNameEdt.text.toString().trim()
        if (lastName.isEmpty()) {
            return "请输入名字"
        }

        val idCode = mBinding.idCodeEdt.text.toString().trim()
        if (idCode.isEmpty()) {
            return "请输入身份证号"
        } else if (!CheckIDCardRule.checkIDCard(idCode)) {
            return "请输入正确的身份证号"
        }

        val phoneCode = mBinding.phoneCodeEdt.text.toString().trim()
        if (phoneCode.isEmpty()) {
            return "请输入手机验证码"
        }
        mModel.value.submitAuthentication(firstName, lastName, phoneCode, idCode)
        return ""
    }
}