package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import androidx.fragment.app.commit
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityWatchBrokerBinding
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ui.fragment.FragmentExplore

class WatchExploreActivity: BaseActivity() {
    private val mBinding = lazy { ActivityWatchBrokerBinding.inflate(layoutInflater) }
    private lateinit var mFragment: FragmentExplore
    override fun getContentView() = mBinding.value.root

    override fun setData(savedInstanceState: Bundle?) {
        super.setData(savedInstanceState)
        mFragment = FragmentExplore()
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(mBinding.value.container.id, mFragment)
        }
        mBinding.value.btnBack.setOnclick {
            finish()
        }
    }

}