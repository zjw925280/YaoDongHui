package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityContractBinding
import net.knowfx.yaodonghui.entities.ContractData
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.toast
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter

class ContractActivity : BaseActivity() {
    private val mBinding = lazy { ActivityContractBinding.inflate(layoutInflater) }

    override fun getContentView() = mBinding.value.root

    override fun setData(savedInstanceState: Bundle?) {
        val isPrivacy = bundle?.getBoolean("isPri", false) ?: false
        if (isPrivacy) {
            commonViewModel.value.getPrivacy()
        } else {
            commonViewModel.value.getUserContract()
        }
        mBinding.value.btnBack.setOnclick {
            finish()
        }
        super.setData(savedInstanceState)
    }

    override fun initViewModel() {
        commonViewModel.value.contract.observe(this) {
            it?.apply {
                result(ContractData(), error = { msg ->
                    msg.toast()
                    finish()
                }, success = { data ->
                    mBinding.value.windowTitle.text = data.labelName
                    mBinding.value.contentTv.apply {
                        setHtml(data.labelValue, HtmlHttpImageGetter(this))
                    }
                })
            }
        }
    }

}