package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import net.knowfx.yaodonghui.databinding.LayoutIndexPagerBinding
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.entities.IndexPagerData
import net.knowfx.yaodonghui.ui.fragment.FragmentIndexVpList
import net.knowfx.yaodonghui.base.BaseActivity

class IndexPagerHolder(parent: ViewGroup, id: Int) : BaseViewHolder(parent, id) {
    private val mFragments = arrayListOf<Fragment?>(null, null, null)
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as IndexPagerData
        val binding = LayoutIndexPagerBinding.bind(itemView)
        binding.pagerVp.offscreenPageLimit = 2
        binding.pagerVp.adapter = MyVpAdapter(itemView.context as BaseActivity)
        itemView.tag = true
        binding.pagerVp.currentItem = 0
        val tabLayoutMediator = TabLayoutMediator(
            binding.pagerTab, binding.pagerVp
        ) { tab, index ->
            when (index) {
                0 -> {
                    tab.text = "最新"
                }

                1 -> {
                    tab.text = "原创"
                }

                else -> {
                    tab.text = "交易商"
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
            return mFragments[position] ?: FragmentIndexVpList(position.toString())
        }
    }
}