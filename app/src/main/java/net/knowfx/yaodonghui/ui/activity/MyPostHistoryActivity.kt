package net.knowfx.yaodonghui.ui.activity

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.ActivityMyPostHistoryBinding
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ui.fragment.FragmentMyPostDealer
import net.knowfx.yaodonghui.ui.fragment.FragmentMyPostExplore
import net.knowfx.yaodonghui.ui.fragment.FragmentMyPostOther
import net.knowfx.yaodonghui.base.BaseActivity

class MyPostHistoryActivity : BaseActivity() {
    private lateinit var mViewBinding: ActivityMyPostHistoryBinding
    private val mFragments = ArrayList<Fragment>()

    override fun getContentView(): View {
        mViewBinding = ActivityMyPostHistoryBinding.inflate(layoutInflater)
        return mViewBinding.root
    }

    

    override fun setData(savedInstanceState: Bundle?) {
        initViews()
    }

    private fun initViews() {
        initFragments()
        mViewBinding.listVp.adapter = MyVpAdapter(this)
        initTab()
        setListeners()
    }

    private fun initFragments() {
        mFragments.add(FragmentMyPostExplore())
        mFragments.add(FragmentMyPostDealer())
        mFragments.add(FragmentMyPostOther())
    }

    private fun initTab() {
        for (i in 0 until 3) {
            val tab = mViewBinding.listTab.getTabAt(i)
            tab?.apply {
                (view[1] as TextView).textSize = resources.getDimension(R.dimen.sp_16)
            }
        }
        mViewBinding.listTab.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.apply {
                    (view[1] as TextView).typeface = Typeface.DEFAULT_BOLD
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.apply {
                    (view[1] as TextView).typeface = Typeface.DEFAULT
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.apply {
                    (view[1] as TextView).typeface = Typeface.DEFAULT_BOLD
                }
            }
        })
        val tabLayoutMediator = TabLayoutMediator(
            mViewBinding.listTab, mViewBinding.listVp
        ) { tab, position ->
            run {
                when (position) {
                    0 -> {
                        tab.text = "曝光列表"
                    }
                    1 -> {
                        tab.text = "交易商评论"
                    }
                    else -> {
                        tab.text = "其他评论"
                    }
                }
            }
        }
        tabLayoutMediator.attach()
    }

    private fun setListeners() {
        mViewBinding.btnBack.setOnclick {
            this.finish()
        }
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