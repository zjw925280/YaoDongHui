package net.knowfx.yaodonghui.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import net.knowfx.yaodonghui.databinding.FragmentMyPostOtherBinding
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.ext.registerLauncher

class FragmentMyPostOther : Fragment() {
    companion object {
        const val STATUS_ALL = -1
        const val STATUS_DENIED = 0
        const val STATUS_PASS = 1
        const val STATUS_WAIT = 2
    }
    private lateinit var mViewBinding: FragmentMyPostOtherBinding
    private val mFragments = ArrayList<Fragment>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mViewBinding = FragmentMyPostOtherBinding.inflate(inflater)
        initViews()
        return mViewBinding.root
    }

    private fun initViews() {
        initFragments()
        mViewBinding.listVp.adapter = MyVpAdapter(requireActivity() as BaseActivity)
        initTab()
    }

    private fun initFragments() {
        mFragments.add(FragmentOtherCommentList(STATUS_ALL))
        mFragments.add(FragmentOtherCommentList(STATUS_PASS))
        mFragments.add(FragmentOtherCommentList(STATUS_WAIT))
        mFragments.add(FragmentOtherCommentList(STATUS_DENIED))
    }

    private fun initTab() {

        mViewBinding.listTab.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.customView?.isSelected = true
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView?.isSelected = false
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.customView?.isSelected = true
            }

        })

        val tabLayoutMediator = TabLayoutMediator(
            mViewBinding.listTab, mViewBinding.listVp
        ) { tab, position ->
            run {
                when (position) {
                    0 -> {
                        tab.text = "全部"
                    }
                    1 -> {
                        tab.text = "已通过"
                    }
                    2 -> {
                        tab.text = "待审核"
                    }
                    else -> {
                        tab.text = "未通过"
                    }
                }
            }
        }
        tabLayoutMediator.attach()
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