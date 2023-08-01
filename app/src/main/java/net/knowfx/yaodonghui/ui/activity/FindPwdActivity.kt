package net.knowfx.yaodonghui.ui.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityFindPwdBinding
import net.knowfx.yaodonghui.ext.checkPwd
import net.knowfx.yaodonghui.ext.logE
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ui.fragment.BaseLoginFragment
import net.knowfx.yaodonghui.ui.fragment.FragmentPhoneCodeCheck
import net.knowfx.yaodonghui.ui.fragment.FragmentPwdDoubleCheck
import net.knowfx.yaodonghui.utils.ToastUtils
import net.knowfx.yaodonghui.viewModels.LoginRegisterViewModel

class FindPwdActivity : BaseActivity() {
    private lateinit var mViewModel: LoginRegisterViewModel
    private lateinit var mBinding: ActivityFindPwdBinding
    private val mFragments = ArrayList<BaseLoginFragment>()
    private var mPhone = ""
    private var mCode = ""
    override fun getContentView(): View {
        mBinding = ActivityFindPwdBinding.inflate(layoutInflater)
        initViews()
        return mBinding.root
    }

    override fun initViewModel() {
        super.initViewModel()
        mViewModel = ViewModelProvider(this)[LoginRegisterViewModel::class.java]
        mViewModel.modifyPwdResult.observe(this) {
            it?.apply {
                if (isSuccess) {
                    data?.trueLet {
                        "修改成功啦".logE()
                        backOnFinish()
                    }
                } else {
                }
            }
        }
    }

    private fun initViews() {
        mFragments.add(FragmentPhoneCodeCheck())
        mFragments.add(FragmentPwdDoubleCheck())
        mBinding.vpLayout.adapter = MyVpAdapter(this)
        mBinding.vpLayout.isUserInputEnabled = false
        mBinding.btnConfirm.setOnclick {
            val params = mFragments[mBinding.vpLayout.currentItem].getParams()
            if (params.isNotEmpty()) {
                if (mBinding.vpLayout.currentItem == 0) {
                    //校验手机号和验证码
                    mPhone = params["phone"] ?: ""
                    mCode = params["code"] ?: ""
                    mBinding.vpLayout.currentItem = 1
                } else {
                    //提交新密码
                    doCommitPwd(params["pwd"] ?: params["pwdAgain"] ?: "")
                }
            }
        }
    }

    private fun doCommitPwd(pwd: String) {
        //请求接口提交新密码
        if (pwd.isEmpty()){
            "请输入正确的密码和确认密码".toast()
            return
        }
        if (!pwd.checkPwd()){
            "密码为6~18位字母数字组合".toast()
            return
        }
        mViewModel.modifyPwd(mPhone, mCode, pwd)
    }

    private fun backOnFinish() {
        setResult(Activity.RESULT_OK, Intent())
        finish()
    }

    fun onBack() {
        (mBinding.vpLayout.currentItem == 1).trueLet {
            (mFragments[1] as FragmentPwdDoubleCheck).cleanInput()
            mBinding.vpLayout.currentItem = 0
        }.elseLet {
            finish()
        }
    }


    /**
     * ViewPager数据适配器
     */
    private inner class MyVpAdapter(fgt: BaseActivity) : FragmentStateAdapter(fgt) {
        override fun getItemCount(): Int {
            return mFragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return if (position > mFragments.size + 1) {
                mFragments[mFragments.size - 1]
            } else {
                mFragments[position]
            }
        }
    }
}