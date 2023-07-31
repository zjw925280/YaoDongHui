package net.knowfx.yaodonghui.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.FragmentMyPostDealerBinding
import net.knowfx.yaodonghui.base.BaseActivity

class FragmentMyPostDealer : Fragment() {
    companion object {
        //1:通过;0:未通过,2待审核;-1全部
        const val STATUS_ALL = -1
        const val STATUS_DENIED = 0
        const val STATUS_PASS = 1
        const val STATUS_WAIT = 2
    }

    private lateinit var mViewBinding: FragmentMyPostDealerBinding
    private val mFragments = ArrayList<Fragment>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mViewBinding = FragmentMyPostDealerBinding.inflate(inflater)
        initViews()
        return mViewBinding.root
    }

    private fun initViews() {
        initFragments()
        mViewBinding.listVp.adapter = MyVpAdapter(requireActivity() as BaseActivity)
        initTab()
    }

    private fun initFragments() {
        mFragments.add(FragmentDealerList(STATUS_ALL))
        mFragments.add(FragmentDealerList(STATUS_PASS))
        mFragments.add(FragmentDealerList(STATUS_WAIT))
        mFragments.add(FragmentDealerList(STATUS_DENIED))
    }

    private fun initTab() {
        val tabLayoutMediator = TabLayoutMediator(
            mViewBinding.listTab, mViewBinding.listVp
        ) { tab, position ->
            run {
                when (position) {
                    0 -> {
                        (tab.view[1] as TextView).textSize = resources.getDimension(R.dimen.sp_32)
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