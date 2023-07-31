package net.knowfx.yaodonghui.ui.activity

import android.os.Bundle
import androidx.fragment.app.commit
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityWatchBrokerBinding
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ui.fragment.FragmentBroker

class WatchBrokerActivity : BaseActivity() {
    private val mBinding = lazy { ActivityWatchBrokerBinding.inflate(layoutInflater) }

    override fun getContentView() = mBinding.value.root
    private lateinit var mFragment: FragmentBroker

    override fun setData(savedInstanceState: Bundle?) {
        super.setData(savedInstanceState)
        mFragment = FragmentBroker()
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(mBinding.value.container.id, mFragment)
        }
        mBinding.value.container.postDelayed({
            mFragment.switchPage(bundle?.getInt("index") ?: 0)
        }, 50)
        mBinding.value.btnBack.setOnclick {
            finish()
        }
    }
}