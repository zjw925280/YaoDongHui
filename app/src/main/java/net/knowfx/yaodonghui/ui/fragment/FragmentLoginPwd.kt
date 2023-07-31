package net.knowfx.yaodonghui.ui.fragment

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.databinding.FragmentLoginPwdBinding
import net.knowfx.yaodonghui.ext.jumpToTargetForResult
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ui.activity.FindPwdActivity
import net.knowfx.yaodonghui.ui.activity.LoginActivity

class FragmentLoginPwd : BaseLoginFragment() {
    private lateinit var mBinding: FragmentLoginPwdBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentLoginPwdBinding.inflate(inflater)
        mBinding.btnPwdSee.setOnclick {
            it.isSelected = !it.isSelected
            it.isSelected.trueLet {
                mBinding.edtPwd.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }.elseLet {
                mBinding.edtPwd.inputType =
                    (InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT)
                mBinding.edtPwd.invalidate()
            }
            val index = mBinding.edtPwd.text.toString().length
            mBinding.edtPwd.setSelection(index, index)
        }

        mBinding.btnForgetPwd.setOnclick {
            jumpToTargetForResult(
                requireActivity(),
                FindPwdActivity::class.java,
                (requireActivity() as LoginActivity).getFindPwdLauncher()
            )
        }
        return mBinding.root
    }

    override fun getParams(): HashMap<String, String> {
        val phone = mBinding.edtPhone.text.toString().trim()
        val pwd = mBinding.edtPwd.text.toString().trim()
        val params = HashMap<String, String>()
        params["type"] = LoginActivity.LOGIN_TYPE_PWD
        if (phone.isEmpty()) {
            params["message"] = "请输入手机号码"
        } else if (phone.length < 11) {
            params["message"] = "请输入正确的手机号码"
        } else {
            params["phone"] = phone
        }
        if (!params["message"].isNullOrEmpty()) {
            return params
        }

        if (pwd.isEmpty()) {
            params["message"] = "请输入密码"
        } else if (pwd.length < 6 || pwd.length > 18) {
            params["message"] = "密码为长度为6~18位的字母，数字和下划线"
        } else {
            params["code"] = pwd
        }
        return params
    }
}