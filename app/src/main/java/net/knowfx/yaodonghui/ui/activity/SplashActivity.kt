package net.knowfx.yaodonghui.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import cn.jiguang.api.utils.JCollectionAuth
import cn.jpush.android.api.JPushInterface
import com.google.gson.Gson
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivitySplashBinding
import net.knowfx.yaodonghui.entities.AdData
import net.knowfx.yaodonghui.entities.ContractData
import net.knowfx.yaodonghui.entities.JUMP_TYPE_CONTENT
import net.knowfx.yaodonghui.entities.JUMP_TYPE_URL
import net.knowfx.yaodonghui.ext.getToken
import net.knowfx.yaodonghui.ext.into
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.readData
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.saveCache
import net.knowfx.yaodonghui.ext.saveData
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.showCenterDialog
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.visible
import net.knowfx.yaodonghui.ui.dialogs.PrivacyDialog

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    private lateinit var mBinding: ActivitySplashBinding
    private val mDefaultCounter = object : CountDownTimer(2000, 1000) {
        override fun onTick(p0: Long) {

        }

        override fun onFinish() {
            jumpToMain()
        }

    }
    private lateinit var mAdCounter: CountDownTimer
    private val mWaitCounter = object: CountDownTimer(5000, 5000){
        override fun onTick(p0: Long) {

        }

        override fun onFinish() {
            jumpToMain()
        }

    }

    override fun isUseTotalFullScreenMode() = true

    override fun getContentView(): View {
        mBinding = ActivitySplashBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initViewModel() {
        commonViewModel.value.contract.observe(this) {
            it?.apply {
                result(ContractData(), error = { msg ->
                    msg.toast()
                    finish()
                }, success = { data ->
                    showCenterDialog(PrivacyDialog(this@SplashActivity, data) { isAgree ->
                        if (isAgree) {
                            saveData("isPrivacy", true)
                            JPushInterface.init(this@SplashActivity)
                            if (getToken().isNotEmpty()){
                                JCollectionAuth.setAuth(this@SplashActivity, true)
                            }
                            commonViewModel.value.getAd()
                            mDefaultCounter.start()
                        } else {
                            finish()
                        }
                    })
                })
            }
        }
        commonViewModel.value.adResult.observe(this) {
            it?.apply {
                if (isSuccess) {
                    data?.apply {
                        mDefaultCounter.cancel()
                        mWaitCounter.start()
                        saveCache("splash_ad",Gson().toJson(this))
                        showAd(this)
                    }
                }
            }
        }
    }


    override fun setData(savedInstanceState: Bundle?) {
        super.setData(savedInstanceState)
        if (!readData("isPrivacy", false)) {
            commonViewModel.value.getPrivacy()
        } else {
            commonViewModel.value.getAd()
            mDefaultCounter.start()
        }

    }

    private fun showAd(adData: AdData) {
        mBinding.adLayout.visible()
        adData.apply {
            mBinding.adImg.into(imageUrl, success = {
                mWaitCounter.cancel()
                startAdCountDown(showTime)
                mBinding.countDown.text = getString(R.string.string_ad_count, showTime)
                mBinding.adImg.setOnclick {
                    val params = HashMap<String, Any>()
                    params["jumpType"] = jumpType
                    when (jumpType) {
                        JUMP_TYPE_CONTENT -> {
                            params["id"] = seekId
                            params["model"] = model
                            mAdCounter.cancel()
                            jumpToMain(params)
                        }

                        JUMP_TYPE_URL -> {
                            params["url"] = jumpUrl
                            mAdCounter.cancel()
                            jumpToMain(params)
                        }

                        else -> {}
                    }
                }
                mBinding.countDown.setOnclick {
                    mAdCounter.cancel()
                    jumpToMain()
                }
            })
        }
    }

    private fun startAdCountDown(seconds: Int) {
        mAdCounter = object : CountDownTimer((seconds * 1000).toLong(), 1000) {
            override fun onTick(remain: Long) {
                mBinding.countDown.text = getString(R.string.string_ad_count, (remain / 1000) + 1)
            }

            override fun onFinish() {
                jumpToMain()
            }
        }
        mAdCounter.start()
    }

    private fun jumpToMain(params: HashMap<String, Any>? = null){
        if (params == null) {
            jumpToTarget(this@SplashActivity, MainActivity::class.java)
        } else {
            jumpToTarget(this@SplashActivity, MainActivity::class.java, params)
        }
        finish()
    }
}