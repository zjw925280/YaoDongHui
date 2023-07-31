package net.knowfx.yaodonghui.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.FragmentExploreBinding
import net.knowfx.yaodonghui.entities.BottomPopData
import net.knowfx.yaodonghui.entities.ExploreTypeData
import net.knowfx.yaodonghui.ext.checkIsLogin
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ui.activity.CommentExplorePostActivity
import net.knowfx.yaodonghui.ui.activity.ExploreSearchActivity
import net.knowfx.yaodonghui.ui.dialogs.DialogBottomList
import net.knowfx.yaodonghui.utils.ExploreTypeEnum

class FragmentExplore : Fragment() {
    private lateinit var mViewBinding: FragmentExploreBinding
    private val mFragments = ArrayList<Fragment>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mViewBinding = FragmentExploreBinding.inflate(inflater)
        initViews()
        return mViewBinding.root
    }

    private fun initViews() {
        mViewBinding.btnExplore.setOnClickListener {
            //弹出选择曝光类型的弹框
            val list = ArrayList<BottomPopData>()
            list.add(BottomPopData(R.mipmap.icon_explore_no_cash, "无法出资"))
            list.add(BottomPopData(R.mipmap.icon_explore_drop, "滑点严重"))
            list.add(BottomPopData(R.mipmap.icon_explore_cheat, "诱导诈骗"))
            list.add(BottomPopData(R.mipmap.icon_explore_other, "其他曝光"))
            DialogBottomList {
                //点击某一种曝光条目，进入曝光页面
                CommentExplorePostActivity.jumpToMe(
                    act = requireActivity(),
                    layoutType = CommentExplorePostActivity.TYPE_EXPLORE,
                    exploreType = it
                )
            }.setDataList(list).show(requireActivity().supportFragmentManager, "")
        }
        mViewBinding.btnExploreSearch.setOnClickListener {
            //搜索
            (requireActivity() as BaseActivity).checkIsLogin {
                jumpToTarget(requireActivity(), ExploreSearchActivity::class.java)
            }
        }
        val list = ArrayList<ExploreTypeData>()
        list.add(
            ExploreTypeData(
                ExploreTypeEnum.TYPE_EXPLORE_ALL.title,
                ExploreTypeEnum.TYPE_EXPLORE_ALL
            )
        )
        list.add(
            ExploreTypeData(
                ExploreTypeEnum.TYPE_EXPLORE_NO_CASH.title,
                ExploreTypeEnum.TYPE_EXPLORE_NO_CASH
            )
        )
        list.add(
            ExploreTypeData(
                ExploreTypeEnum.TYPE_EXPLORE_DROP_FAST.title,
                ExploreTypeEnum.TYPE_EXPLORE_DROP_FAST
            )
        )
        list.add(
            ExploreTypeData(
                ExploreTypeEnum.TYPE_EXPLORE_CHEAT.title,
                ExploreTypeEnum.TYPE_EXPLORE_CHEAT
            )
        )
        list.add(
            ExploreTypeData(
                ExploreTypeEnum.TYPE_EXPLORE_OTHER.title,
                ExploreTypeEnum.TYPE_EXPLORE_OTHER
            )
        )
        createPages(list)
    }


    private fun createPages(list: ArrayList<ExploreTypeData>) {
        list.forEach {
            mFragments.add(FragmentExploreList(it.type))
        }
        mViewBinding.exploreVp.adapter = MyVpAdapter(activity as BaseActivity)
        val tabLayoutMediator = TabLayoutMediator(
            mViewBinding.exploreTabs, mViewBinding.exploreVp
        ) { tab, position ->
            tab.text = list[position].title
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