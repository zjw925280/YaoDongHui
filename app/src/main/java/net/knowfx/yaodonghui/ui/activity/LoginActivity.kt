package net.knowfx.yaodonghui.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityLoginBinding
import net.knowfx.yaodonghui.entities.TokenData
import net.knowfx.yaodonghui.entities.UserInfoData
import net.knowfx.yaodonghui.ext.falseLet
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.jumpToTargetForResult
import net.knowfx.yaodonghui.ext.registerLauncher
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.saveToken
import net.knowfx.yaodonghui.ext.saveUserData
import net.knowfx.yaodonghui.ext.setMultipleClick
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ui.fragment.BaseLoginFragment
import net.knowfx.yaodonghui.ui.fragment.FragmentLoginPhone
import net.knowfx.yaodonghui.ui.fragment.FragmentLoginPwd
import net.knowfx.yaodonghui.utils.ClickControlUtil
import net.knowfx.yaodonghui.utils.ToastUtils
import net.knowfx.yaodonghui.viewModels.LoginRegisterViewModel

class LoginActivity : BaseActivity() {
    companion object {
        const val LOGIN_TYPE_PWD = "type_pwd"
        const val LOGIN_TYPE_PHONE = "type_phone"
    }

    private lateinit var registerLauncher: ActivityResultLauncher<Intent>
    private lateinit var findPwdLauncher: ActivityResultLauncher<Intent>
    private val mFragments = ArrayList<BaseLoginFragment>()
    private lateinit var mBinding: ActivityLoginBinding
    private lateinit var mViewModel: LoginRegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        registerLauncher = registerLauncher {
            if (it.resultCode == RESULT_OK) {
                "注册成功，请您使用注册的账号登录".toast()
            }
        }
        findPwdLauncher = registerLauncher {
            if (it.resultCode == RESULT_OK) {
                "密码修改成功，请使用新密码登录".toast()
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun initViewModel() {
        super.initViewModel()
        mViewModel = ViewModelProvider(this)[LoginRegisterViewModel::class.java]
        mViewModel.loginResult.observe(this) {
            it?.apply {
                result(TokenData(), error = { msg ->
                    msg.toast()
                }, success = { data ->
                    data.token.saveToken()
                    mViewModel.requestUserInfo()
                })
            }
        }
        mViewModel.userInfoResult.observe(this) {
            it?.apply {
                result(UserInfoData(), success = { data ->
                    backForResult(data)
                }, error = {
                    "登录失败，请稍后重试".toast()
                })
            }
        }
    }

    override fun getContentView(): View {
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        initViews()
        return mBinding.root
    }

    fun getFindPwdLauncher(): ActivityResultLauncher<Intent> {
        return findPwdLauncher
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
                if (ClickControlUtil.getInstance().isClickable()) {
                    //打开用户协议
                    jumpToTarget(
                        this@LoginActivity,
                        ContractActivity::class.java,
                        hashMapOf(Pair("isPri", false))
                    )
                }
            }

        }
        val privacySpannable = object : ClickableSpan() {
            override fun onClick(p0: View) {
                if (ClickControlUtil.getInstance().isClickable()) {
                    //打开隐私策略
                    jumpToTarget(
                        this@LoginActivity,
                        ContractActivity::class.java,
                        hashMapOf(Pair("isPri", true))
                    )
                }
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
        //界面初始化
        initFragments()
        addListeners()
    }

    private fun initFragments() {
        mFragments.add(FragmentLoginPwd())
        mFragments.add(FragmentLoginPhone())
        mBinding.vpLogin.adapter = MyVpAdapter(this)
        mBinding.vpLogin.isUserInputEnabled = false
        setTab(mBinding.btnLoginPwd)
    }

    private fun addListeners() {
        setMultipleClick(
            mBinding.btnLoginPwd,
            mBinding.btnLoginPhone,
            mBinding.btnBack,
            mBinding.btnLogin,
            mBinding.btnRegister,
            mBinding.radioContract
        ) {
            when (it) {
                mBinding.btnLoginPwd, mBinding.btnLoginPhone -> {
                    setTab(it)
                }

                mBinding.btnBack -> {
                    finish()
                }

                mBinding.btnLogin -> {
                    //登录
                    doLogin()
                }

                mBinding.btnRegister -> {
                    //跳转到注册页面
                    jumpToTargetForResult(this, RegisterActivity::class.java, registerLauncher)
                }

                mBinding.radioContract -> {
                    mBinding.radioContract.isSelected = !mBinding.radioContract.isSelected
                }

                else -> {}
            }
        }
    }

    private fun setTab(view: View) {
        if ((if (mBinding.vpLogin.currentItem == 0) mBinding.btnLoginPwd else mBinding.btnLoginPhone) == view) {
            return
        }
        when (view) {
            mBinding.btnLoginPwd -> {
                mBinding.btnLoginPwd.setTextAppearance(R.style.TabTextStyleSelected)
                mBinding.btnLoginPhone.setTextAppearance(R.style.TabTextStyleNormal)
                mBinding.vpLogin.currentItem = 0
            }

            else -> {
                mBinding.btnLoginPwd.setTextAppearance(R.style.TabTextStyleNormal)
                mBinding.btnLoginPhone.setTextAppearance(R.style.TabTextStyleSelected)
                mBinding.vpLogin.currentItem = 1
            }
        }
    }

    private fun doLogin() {
        val curPosition = mBinding.vpLogin.currentItem
        val curFragment = mFragments[curPosition]
        val params = curFragment.getParams()
        params.isNotEmpty().trueLet {
            if (params.containsKey("message")) {
                params["message"]?.toast()
                return
            } else if (params.containsKey("phone") && params.containsKey("code")) {
                mBinding.radioContract.isSelected.falseLet {
                    "请阅读并同意下方的用户协议和隐私政策".toast()
                    return
                }
                mUserPhone = params["phone"] ?: ""
                if (params["type"] == LOGIN_TYPE_PWD) {
                    loginWithPwd(params["phone"] ?: "", params["code"] ?: "")
                } else {
                    loginWithPhone(params["phone"] ?: "", params["code"] ?: "")
                }
            }
        }
    }

    private var mUserPhone = ""

    private fun loginWithPwd(phone: String, pwd: String) {
        if (phone.isEmpty() || pwd.isEmpty()) {
            "请输入正确的手机号和密码".toast()
            return
        }
        //请求接口
        mViewModel.loginWithPwd(phone, pwd)
    }

    private fun loginWithPhone(phone: String, code: String) {
        if (phone.isEmpty() || code.isEmpty()) {
            "请输入正确的手机号和验证码".toast()
            return
        }
        //请求接口
        mViewModel.loginWithCode(phone, code)
    }

    private fun backForResult(data: UserInfoData) {
        data.saveUserData()
        val intent = Intent()
        intent.putExtra("user", data)
        setResult(Activity.RESULT_OK, intent)
        this.finish()
    }

    /**
     * ViewPager数据适配器
     */
    inner class MyVpAdapter(fgt: BaseActivity) : FragmentStateAdapter(fgt) {
        override fun getItemCount(): Int {
            return mFragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return if (position > mFragments.size - 1) {
                mFragments[mFragments.size - 1]
            } else {
                mFragments[position]
            }
        }
    }
}