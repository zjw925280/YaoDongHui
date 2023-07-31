package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityBrandEventBinding
import net.knowfx.yaodonghui.entities.BrandEventData
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.viewModels.SettingViewModel

class BrandEventActivity : BaseActivity() {
    private lateinit var mBinding: ActivityBrandEventBinding
    private val mModel = lazy { ViewModelProvider(this)[SettingViewModel::class.java] }
    private val mAdapter = CommonListAdapter<BrandEventData>()

    override fun getContentView(): View {
        mBinding = ActivityBrandEventBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initViewModel() {
        super.initViewModel()
        mModel.value.eventList.observe(this) {
            it?.apply {
                result(ArrayList<BrandEventData>(), error = { msg ->

                }, success = { list ->
                    mAdapter.putData(list)
                })
            }
        }
    }

    override fun setData(savedInstanceState: Bundle?) {
        super.setData(savedInstanceState)
        mBinding.btnBack.setOnclick {
            finish()
        }
        mBinding.rvEvent.layoutManager = LinearLayoutManager(this)
        mBinding.rvEvent.adapter = mAdapter
        mModel.value.getBrandEvent()
    }
}