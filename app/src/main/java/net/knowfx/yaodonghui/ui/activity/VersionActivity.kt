package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityVersionBinding
import net.knowfx.yaodonghui.entities.VersionData
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.viewModels.SettingViewModel

class VersionActivity : BaseActivity() {
    private val mBinding = lazy { ActivityVersionBinding.inflate(layoutInflater) }
    private val mModel = lazy { ViewModelProvider(this)[SettingViewModel::class.java] }
    private val mAdapter = lazy { CommonListAdapter<VersionData>() }
    override fun getContentView() = mBinding.value.root

    override fun initViewModel() {
        super.initViewModel()
        mModel.value.versionList.observe(this) {
            it?.apply {
                result(ArrayList<VersionData>(), error = { msg ->
                    msg.toast()
                }, success = { list ->
                    mAdapter.value.putData(dataList = list)
                })
            }
        }
    }

    override fun setData(savedInstanceState: Bundle?) {
        super.setData(savedInstanceState)
        mBinding.value.rvVersion.layoutManager = LinearLayoutManager(this)
        mBinding.value.rvVersion.adapter = mAdapter.value
        mModel.value.getVersions()
        mBinding.value.btnBack.setOnclick {
            finish()
        }
    }
}