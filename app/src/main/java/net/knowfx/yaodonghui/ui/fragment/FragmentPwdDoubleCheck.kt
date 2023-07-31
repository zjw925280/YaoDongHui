package net.knowfx.yaodonghui.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.databinding.FragmentPwdDoubleCheckBinding
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ui.activity.FindPwdActivity
import net.knowfx.yaodonghui.utils.ToastUtils


class FragmentPwdDoubleCheck : BaseLoginFragment() {
    private lateinit var mBinding: FragmentPwdDoubleCheckBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPwdDoubleCheckBinding.inflate(inflater)
        initViews()
        return mBinding.root
    }

    private fun initViews() {
        mBinding.btnBack.setOnclick {
            (requireActivity() as FindPwdActivity).onBack()
        }
    }

    override fun getParams(): HashMap<String, String> {
        val result = checkParams()
        val params = HashMap<String, String>()
        result.isEmpty().trueLet {
            params["pwd"] = mBinding.layoutPwdCheck.edtPwd.text.toString()
            params["pwdAgain"] = mBinding.layoutPwdCheck.edtPwdAgain.text.toString()
        }.elseLet {
            result.toast()
        }
        return params
    }

    private fun checkParams(): String {
        val pwd = mBinding.layoutPwdCheck.edtPwd.text.toString()
        val pwdAgain = mBinding.layoutPwdCheck.edtPwdAgain.text.toString()
        if (pwd.isEmpty()) return "请输入密码"
        if (pwdAgain.isEmpty()) return "请输入确认密码"
        if (pwd != pwdAgain) return "两次输入密码不一致，请确认密码输入"
        return ""
    }

    fun cleanInput() {
        mBinding.layoutPwdCheck.edtPwd.setText("")
        mBinding.layoutPwdCheck.edtPwdAgain.setText("")
    }
}