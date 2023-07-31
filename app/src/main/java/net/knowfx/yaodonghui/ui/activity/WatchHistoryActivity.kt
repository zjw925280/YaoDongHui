package net.knowfx.yaodonghui.ui.activity

import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityWatchHistoryBinding
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ui.fragment.FragmentRankHistory
import net.knowfx.yaodonghui.ui.fragment.FragmentTopWatch
import net.knowfx.yaodonghui.ui.fragment.FragmentWatchRank

class WatchHistoryActivity : BaseActivity() {
    private val mBinding = lazy { ActivityWatchHistoryBinding.inflate(layoutInflater) }
    private val mFragments = ArrayList<Fragment>()

    override fun getContentView() = mBinding.value.root

    override fun setData(savedInstanceState: Bundle?) {
        super.setData(savedInstanceState)
        initFragments()
        initTab()
        mBinding.value.btnBack.setOnclick {
            finish()
        }
    }


    private fun initFragments() {
        mFragments.add(FragmentTopWatch())
        mFragments.add(FragmentWatchRank())
        mFragments.add(FragmentRankHistory())
    }

    private fun initTab() {
        mBinding.value.apply {
            pagerVp.adapter = MyVpAdapter(this@WatchHistoryActivity)
            pagerTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.apply {
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
                pagerTab, pagerVp
            ) { tab, position ->
                run {
                    when (position) {
                        0 -> {
                            tab.text = "人气访问"
                        }

                        1 -> {
                            tab.text = "访问排行"
                        }

                        else -> {
                            tab.text = "浏览历史"
                        }
                    }
                }
            }
            tabLayoutMediator.attach()
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