package net.knowfx.yaodonghui.ui.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.FragmentLoginPhoneBinding
import net.knowfx.yaodonghui.ext.dismissLoadingDialog
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.showLoadingDialog
import net.knowfx.yaodonghui.ext.startCountDownForGetCode
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ui.activity.LoginActivity.Companion.LOGIN_TYPE_PHONE
import net.knowfx.yaodonghui.utils.ToastUtils
import net.knowfx.yaodonghui.viewModels.LoginRegisterViewModel
import org.json.JSONObject

class FragmentLoginPhone : BaseLoginFragment() {
    private lateinit var mBinding: FragmentLoginPhoneBinding
    private lateinit var mViewModel: LoginRegisterViewModel
    private var uuid=""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentLoginPhoneBinding.inflate(inflater)
        initViewModel()
        initViews()
        return mBinding.root
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(this)[LoginRegisterViewModel::class.java]

        mViewModel.getGraphicCode()
        mBinding.btnGetGraphicCode.setOnClickListener(View.OnClickListener {
            mViewModel.getGraphicCode()
        })
        mViewModel.graphicCodeResult.observe(this) {
            //请求图形验证码成功，开始倒计时
            dismissLoadingDialog()
            val jsonObject = JSONObject(Gson().toJson(it))
            uuid = jsonObject.getString("uuid")
            val img = jsonObject.getString("img")
            val code = base64ToBitmap(img)
            mBinding.btnGetGraphicCode.setImageBitmap(code)
        }

        mViewModel.phoneUuidCodeResult.observe(this) {
            //请求手机验证码成功，开始倒计时
            dismissLoadingDialog()
            it?.apply {
                result(String(), error = { msg ->
                    msg.toast()
                }, success = {
                    mBinding.btnGetCode.startCountDownForGetCode()
                })
            } ?: {
                "获取手机验证码失败，请稍后重试".toast()
            }
        }
    }
    fun base64ToBitmap(base64String: String): Bitmap? {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
    private fun initViews() {
        mBinding.btnGetCode.setOnclick {
            val result = checkPhone()
            if (!result.first) {
                result.second.toast()
                return@setOnclick
            }
            showLoadingDialog()
            //获取手机验证码
            var phone=mBinding.edtPhone.text.toString().trim()
            var code=mBinding.edtGraphicCode.text.toString().trim()

            mViewModel.requestUuidPhoneCode(phone,code,uuid,"")
        }
    }

    override fun getParams(): HashMap<String, String> {
        val code = mBinding.edtPwd.text.toString().trim()
        val params = HashMap<String, String>()
        params["type"] = LOGIN_TYPE_PHONE
        val result = checkPhone()
        params[if (result.first) "phone" else "message"] = result.second
        if (!result.first) {
            return params
        }

        if (code.isEmpty()) {
            params["message"] = "请输入手机验证码"
        } else {
            params["code"] = code
        }
        return params
    }

    private fun checkPhone(): Pair<Boolean, String> {
        val phone = mBinding.edtPhone.text.toString().trim()
        return if (phone.isEmpty()) {
            Pair(false, "请输入手机号码")
        } else if (phone.length < 11) {
            Pair(false, "请输入正确的手机号码")
        } else {
            Pair(true, phone)
        }
    }
}