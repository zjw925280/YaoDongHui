package net.knowfx.yaodonghui.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import cn.jiguang.api.utils.JCollectionAuth
import com.google.gson.Gson
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.ActivityMainBinding
import net.knowfx.yaodonghui.ui.fragment.FragmentBroker
import net.knowfx.yaodonghui.ui.fragment.FragmentExplore
import net.knowfx.yaodonghui.ui.fragment.FragmentMine
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.entities.JUMP_TYPE_CONTENT
import net.knowfx.yaodonghui.entities.JUMP_TYPE_NOTHING
import net.knowfx.yaodonghui.entities.JUMP_TYPE_URL
import net.knowfx.yaodonghui.entities.TokenData
import net.knowfx.yaodonghui.entities.UserInfoData
import net.knowfx.yaodonghui.ext.getToken
import net.knowfx.yaodonghui.ext.jumpFromPush
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.jumpToTargetForResult
import net.knowfx.yaodonghui.ext.registerLauncher
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.saveToken
import net.knowfx.yaodonghui.ext.saveUserData
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ui.fragment.FragmentNewIndex
import net.knowfx.yaodonghui.utils.MyApplication
import kotlin.math.max

class MainActivity : BaseActivity(), OnClickListener {
    private lateinit var mViewBinding: ActivityMainBinding
    private var mCurrentFragment: Fragment? = null
    private val mFragments = ArrayList<Fragment>()
    private val mBtnIcons = ArrayList<AppCompatImageView>()
    private val mBtnTexts = ArrayList<AppCompatTextView>()
    lateinit var loginLauncher: ActivityResultLauncher<Intent>
    private lateinit var registerLauncher: ActivityResultLauncher<Intent>
    private lateinit var mReceiver: IndexJumpReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        loginLauncher = registerLauncher { result ->
            result.data?.apply {
                (result.resultCode != Activity.RESULT_OK).trueLet {
                    return@registerLauncher
                }
                JCollectionAuth.setAuth(MyApplication.getInstance(), true)
                if (mCurrentFragment is FragmentMine) {
                    (mCurrentFragment as FragmentMine).refreshData()
                }
            }
        }

        registerLauncher = registerLauncher {
            if (it.resultCode == RESULT_OK) {
                "注册成功，请您使用注册的账号登录应用".toast()
                jumpToLoginActivity()
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun getContentView(): View {
        mViewBinding = ActivityMainBinding.inflate(layoutInflater)
        return mViewBinding.root
    }


    override fun isUseFullScreenMode() = true

    override fun isUseBlackText() = true

    override fun setData(savedInstanceState: Bundle?) {
        super.setData(savedInstanceState)
        when (bundle?.getString("jumpType") ?: JUMP_TYPE_NOTHING) {
            JUMP_TYPE_URL -> {
                val url = bundle?.getString("url") ?: ""
                if (url.isNotEmpty()) {
                    jumpToTarget(
                        this, WebActivity::class.java, hashMapOf(
                            Pair("url", url),
                            Pair("title", "广告")
                        )
                    )
                }
            }

            JUMP_TYPE_CONTENT -> {
                val id = bundle?.getInt("id") ?: 0
                val model = bundle?.getString("model") ?: ""
                (id > 0 && model.isNotEmpty()).trueLet {
                    jumpFromPush(model, id)
                }
            }

            else -> {}
        }
    }

    @SuppressLint("CommitTransaction")
    private fun initViews() {
        mBtnIcons.add(mViewBinding.btn1)
        mBtnIcons.add(mViewBinding.btn2)
        mBtnIcons.add(mViewBinding.btn3)
        mBtnIcons.add(mViewBinding.btn4)

        mBtnTexts.add(mViewBinding.text1)
        mBtnTexts.add(mViewBinding.text2)
        mBtnTexts.add(mViewBinding.text3)
        mBtnTexts.add(mViewBinding.text4)
        // 添加首页的Fragment
        mFragments.add(FragmentNewIndex())
        mFragments.add(FragmentBroker())
        mFragments.add(FragmentExplore())
        mFragments.add(FragmentMine())

        switchFragment(mFragments[0])

        mViewBinding.btnLayout1.setOnClickListener(this)
        mViewBinding.btnLayout2.setOnClickListener(this)
        mViewBinding.btnLayout3.setOnClickListener(this)
        mViewBinding.btnLayout4.setOnClickListener(this)

    }

    override fun initViewModel() {
        super.initViewModel()
        commonViewModel.value.token.observe(this) {
            it?.result(TokenData(), error = {
                "身份信息已过期，请重新登录".toast()
                clearLoginUser()
                initViews()
            }, success = { data ->
                if (data.token.isEmpty()) {
                    "身份信息已过期，请重新登录".toast()
                    clearLoginUser()
                    initViews()
                    return@observe
                }
                data.token.saveToken()
                commonViewModel.value.requestUserInfo()
            }) ?: apply {
                "身份信息已过期，请重新登录".toast()
                clearLoginUser()
                initViews()
            }
        }
        commonViewModel.value.userInfoResult.observe(this) {
            it?.apply {
                result(UserInfoData(), success = { data ->
                    Log.e("数据平台","数据平台="+Gson().toJson(data))
                    data.saveUserData()
                    if (mCurrentFragment is FragmentMine) {
                        (mCurrentFragment as FragmentMine).refreshData()
                    }
                }, error = { msg ->
                    if (msg != "用户名或密码不存在！") {
                        msg.toast()
                    } else {
                        "身份信息已过期，请重新登录".toast()
                        clearLoginUser()
                    }
                })
            }
            initViews()
        }
        getToken().isNotEmpty().trueLet {
            commonViewModel.value.refreshToken()
        }.elseLet {
            initViews()
        }
    }

    @SuppressLint("CommitTransaction")
    private fun switchFragment(targetFragment: Fragment) {
        if (targetFragment === mCurrentFragment) {
            return
        }
        val transaction = supportFragmentManager.beginTransaction()
        if (!targetFragment.isAdded) {
            if (mCurrentFragment != null) {
                transaction.hide(mCurrentFragment!!)
            }
            transaction.add(R.id.fragmentContainer, targetFragment, targetFragment.javaClass.name)
        } else {
            transaction.hide(mCurrentFragment!!).show(targetFragment)
        }
        transaction.commit()
        switchBtn(mFragments.indexOf(targetFragment), mFragments.indexOf(mCurrentFragment))
        mCurrentFragment = targetFragment
        when (mCurrentFragment) {
            mFragments.last() -> {
                this.window.statusBarColor = Color.parseColor("#E1D5F2")
            }

            else -> {
                this.window.statusBarColor = Color.WHITE
            }
        }

    }

    private fun switchBtn(currentIndex: Int, lastIndex: Int) {
        mBtnIcons[max(lastIndex, 0)].isSelected = false
        mBtnTexts[max(lastIndex, 0)].setTextColor(Color.parseColor("#999999"))
        mBtnIcons[max(currentIndex, 0)].isSelected = true
        mBtnTexts[max(currentIndex, 0)].setTextColor(
            ContextCompat.getColor(
                this, R.color.color_9C67E6
            )
        )
    }

    override fun onClick(v: View?) {
        v?.apply {
            when (this) {
                mViewBinding.btnLayout1 -> {
                    switchFragment(mFragments[0])
                }

                mViewBinding.btnLayout2 -> {
                    switchFragment(mFragments[1])
                }

                mViewBinding.btnLayout3 -> {
                    switchFragment(mFragments[2])
                }

                mViewBinding.btnLayout4 -> {
                    switchFragment(mFragments[3])
                }

                else -> {}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mReceiver = IndexJumpReceiver()
        registerReceiver(mReceiver, IntentFilter("index_jump"))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mReceiver)
    }

    fun jumpToLoginActivity() {
        jumpToTargetForResult(this, LoginActivity::class.java, loginLauncher)
    }

    fun jumpToRegisterActivity() {
        jumpToTargetForResult(this, RegisterActivity::class.java, registerLauncher)
    }

    inner class IndexJumpReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, data: Intent?) {
            data?.apply {
                (action == "index_jump").trueLet {
                    val code = getStringExtra("code") ?: ""
                    code.isNotEmpty().trueLet {
                        when (code) {

                            else -> {
                                "功能类型不支持！".toast()
                            }
                        }
                    }
                }
            }
        }

    }
}