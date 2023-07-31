package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import android.text.Html.ImageGetter
import androidx.lifecycle.ViewModelProvider
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityProfileBinding
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.viewModels.SettingViewModel
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter

class ProfileActivity : BaseActivity() {
    private val mBinding = lazy { ActivityProfileBinding.inflate(layoutInflater) }
    private val mModel = lazy { ViewModelProvider(this)[SettingViewModel::class.java] }
    override fun getContentView() = mBinding.value.root

    override fun initViewModel() {
        super.initViewModel()
        mModel.value.profile.observe(this) {
            it?.apply {
                result(String(), error = { msg -> msg.toast() }, success = { str ->
                    mBinding.value.textContent.setHtml(str, HtmlHttpImageGetter(mBinding.value.textContent))
                })
            }
        }
    }

    override fun setData(savedInstanceState: Bundle?) {
        super.setData(savedInstanceState)
        mModel.value.getProfile()
        mBinding.value.btnBack.setOnclick { finish() }
    }
}