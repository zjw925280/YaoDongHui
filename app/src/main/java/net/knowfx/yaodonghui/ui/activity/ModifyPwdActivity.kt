package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import net.knowfx.yaodonghui.databinding.ActivityModifyPwdBinding
import net.knowfx.yaodonghui.ext.setMultipleClick
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.ext.checkPwd
import net.knowfx.yaodonghui.ext.logE
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.utils.ToastUtils
import net.knowfx.yaodonghui.viewModels.LoginRegisterViewModel

class ModifyPwdActivity : BaseActivity() {
    private lateinit var mViewBinding: ActivityModifyPwdBinding
    private lateinit var mViewModel: LoginRegisterViewModel

    override fun getContentView(): View {
        mViewBinding = ActivityModifyPwdBinding.inflate(layoutInflater)
        return mViewBinding.root
    }

    override fun initViewModel() {
        super.initViewModel()
        mViewModel = ViewModelProvider(this)[LoginRegisterViewModel::class.java]
        mViewModel.modifyPwdResult.observe(this) {
            it?.apply {
                if (isSuccess) {
                    data?.trueLet {
                        "修改密码成功".toast()
                        logout()
                    }
                } else {
                }
            }
        }
    }

    override fun setData(savedInstanceState: Bundle?) {
        setMultipleClick(mViewBinding.btnBack, mViewBinding.btnConfirm) {
            when (it) {
                mViewBinding.btnBack -> {
                    this.finish()
                }

                else -> {
                    val result = checkPwd()
                    if (result.contains("|")) {
                        //请求接口
                        val array = result.split("|")
                        mViewModel.changePwd(array[0], array[1])
                        this.finish()
                    } else {
                        result.toast()
                    }
                }
            }
        }
        super.setData(savedInstanceState)
    }

    private fun checkPwd(): String {
        val oldPwd = mViewBinding.edtOldPwd.text.toString().trim()
        val newPwd = mViewBinding.edtNewPwd.text.toString().trim()
        val confirmPwd = mViewBinding.edtConfirmPwd.text.toString().trim()
        return when {
            oldPwd.isEmpty() -> {
                "请输入原密码"
            }

            !oldPwd.checkPwd() -> {
                "原密码必须为6~18位"
            }

            newPwd.isEmpty() -> {
                "请输入新密码"
            }
            !newPwd.checkPwd() -> {
                "密码为6~18位字母数字组合"
            }

            newPwd.length < 6 || newPwd.length > 18 -> {
                "新密码必须为6~18位"
            }

            confirmPwd.isEmpty() -> {
                "请输入确认密码"
            }

            confirmPwd.length < 6 || confirmPwd.length > 18 -> {
                "确认密码必须为6~18位"
            }

            newPwd != confirmPwd -> {
                "新密码与确认密码不同，请重新输入"
            }

            else -> {
                "$oldPwd|$newPwd"
            }
        }
    }
}