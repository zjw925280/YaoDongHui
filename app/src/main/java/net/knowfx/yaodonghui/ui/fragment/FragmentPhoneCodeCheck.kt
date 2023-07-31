package net.knowfx.yaodonghui.ui.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import net.knowfx.yaodonghui.databinding.FragmentPhoneCheckBinding
import net.knowfx.yaodonghui.ext.dismissLoadingDialog
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setMultipleClick
import net.knowfx.yaodonghui.ext.showLoadingDialog
import net.knowfx.yaodonghui.ext.startCountDownForGetCode
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ui.activity.FindPwdActivity
import net.knowfx.yaodonghui.utils.ToastUtils
import net.knowfx.yaodonghui.viewModels.LoginRegisterViewModel

class FragmentPhoneCodeCheck : BaseLoginFragment() {
    private lateinit var mViewModel: LoginRegisterViewModel
    private lateinit var mBinding: FragmentPhoneCheckBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPhoneCheckBinding.inflate(inflater)
        initViews()
        return mBinding.root
    }

    override fun getParams(): HashMap<String, String> {
        val result = checkParams()
        val params = HashMap<String, String>()
        if (result.isEmpty()) {
            params["phone"] = mBinding.layoutPhoneCheck.edtPhone.text.toString().trim()
            params["code"] = mBinding.layoutPhoneCheck.edtCode.text.toString().trim()
        } else {
            result.toast()
        }
        return params
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(this)[LoginRegisterViewModel::class.java]
        mViewModel.phoneCodeResult.observe(this) {
            //请求手机验证码成功，开始倒计时
            dismissLoadingDialog()
            it?.apply {
                it.result(String(), error = { msg ->
                    msg.toast()
                }, success = {
                    mBinding.layoutPhoneCheck.btnGetCode.startCountDownForGetCode()
                })
            }
        }
    }

    private fun initViews() {
        initViewModel()
        setMultipleClick(mBinding.btnBack, mBinding.layoutPhoneCheck.btnGetCode) {
            when (it) {
                mBinding.btnBack -> {
                    (requireActivity() as FindPwdActivity).onBack()
                }

                mBinding.layoutPhoneCheck.btnGetCode -> {
                    val result = checkPhone()
                    if (!result.first) {
                        result.second.toast()
                        return@setMultipleClick
                    }
                    //获取手机验证码
                    showLoadingDialog()
                    mViewModel.requestPhoneCode(
                        phone = mBinding.layoutPhoneCheck.edtPhone.text.toString().trim()
                    )
                }

                else -> {}
            }
        }
    }


    private fun checkParams(): String {
        val result = checkPhone()
        if (!result.first) return result.second
        if (mBinding.layoutPhoneCheck.edtCode.text.toString().trim()
                .isEmpty()
        ) return "请输入手机验证码"
        return ""
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