package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import net.knowfx.yaodonghui.databinding.ActivityAboutBinding
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.setMultipleClick

class AboutActivity : BaseActivity() {
    private lateinit var mViewBinding: ActivityAboutBinding

    override fun setData(savedInstanceState: Bundle?) {
        super.setData(savedInstanceState)
        val verStr = packageManager.getPackageInfo(packageName, 0).versionName
        mViewBinding.textVersion.text = verStr
        mViewBinding.labelVersion.text = verStr
        addListeners()
    }

    override fun getContentView(): View {
        Log.e("ralph", "width === ${resources.displayMetrics.widthPixels} && height === ${resources.displayMetrics.heightPixels}")
        mViewBinding = ActivityAboutBinding.inflate(layoutInflater)
        return mViewBinding.root
    }

    private fun addListeners() {
        setMultipleClick(
            mViewBinding.btnBack,
            mViewBinding.textBigMoment,
            mViewBinding.textProfile,
            mViewBinding.textInfo,
            mViewBinding.textContract,
            mViewBinding.textPrivacy
        ) {
            when (it) {
                mViewBinding.textBigMoment -> {
                    jumpToTarget(this, BrandEventActivity::class.java)
                }

                mViewBinding.textProfile -> {
                    jumpToTarget(this, VersionActivity::class.java)
                }

                mViewBinding.textInfo -> {
                    jumpToTarget(this, ProfileActivity::class.java)
                }

                mViewBinding.textContract, mViewBinding.textPrivacy -> {
                    jumpToTarget(
                        this,
                        ContractActivity::class.java,
                        hashMapOf(Pair("isPri", it == mViewBinding.textPrivacy))
                    )
                }

                else -> {
                    finish()
                }
            }
        }
    }
}