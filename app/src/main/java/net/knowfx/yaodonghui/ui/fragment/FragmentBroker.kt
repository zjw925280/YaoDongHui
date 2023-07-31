package net.knowfx.yaodonghui.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.FragmentBrokerBinding
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ui.activity.BrokerSearchActivity

class FragmentBroker : Fragment() {
    private lateinit var mViewBinding: FragmentBrokerBinding
    private val mFragments = ArrayList<Fragment>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mViewBinding = FragmentBrokerBinding.inflate(inflater)
        mViewBinding.brokerVp.offscreenPageLimit = 1
        mFragments.add(FragmentListBrokerRank(FragmentListBrokerRank.TYPE_SUPERVISE_RANK))
        mFragments.add(FragmentListBrokerRank(FragmentListBrokerRank.TYPE_BRAND_RANK))
        mFragments.add(FragmentListBrokerRank(FragmentListBrokerRank.TYPE_BLOCK_RANK))
        mViewBinding.brokerVp.adapter = MyVpAdapter(activity as BaseActivity)
        val tabLayoutMediator = TabLayoutMediator(
            mViewBinding.brokerTabs, mViewBinding.brokerVp
        ) { tab, position ->
            run {
                when (position) {
                    0 -> {
                        tab.text = getString(R.string.string_supervise_rank)
                    }

                    1 -> {
                        tab.text = getString(R.string.string_brand_rank)
                    }

                    else -> {
                        tab.text = getString(R.string.string_black_rank)
                    }
                }
            }
        }
        tabLayoutMediator.attach()
        mViewBinding.btnBrokerSearch.setOnclick {
            jumpToTarget(requireActivity(), BrokerSearchActivity::class.java, hashMapOf(Pair("isSelect", false)))
        }
        return mViewBinding.root
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

    fun switchPage(index: Int) {
        mViewBinding.brokerVp.setCurrentItem(index,false)
    }


}