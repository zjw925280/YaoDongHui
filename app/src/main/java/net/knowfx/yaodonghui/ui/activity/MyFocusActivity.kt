package net.knowfx.yaodonghui.ui.activity

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.base.BaseSelectFragment
import net.knowfx.yaodonghui.databinding.ActivityMyFocusBinding
import net.knowfx.yaodonghui.ext.dismissLoadingDialog
import net.knowfx.yaodonghui.ext.getSplitString
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.setMultipleClick
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.showLoadingDialog
import net.knowfx.yaodonghui.ext.toast
import net.knowfx.yaodonghui.ext.visible
import net.knowfx.yaodonghui.ui.fragment.FragmentMyFocusArticle
import net.knowfx.yaodonghui.ui.fragment.FragmentMyFocusDealer

class MyFocusActivity : BaseActivity() {
    private lateinit var mViewBinding: ActivityMyFocusBinding
    private val mFragments = ArrayList<BaseSelectFragment>()

    override fun getContentView(): View {
        mViewBinding = ActivityMyFocusBinding.inflate(layoutInflater)
        return mViewBinding.root
    }

    override fun initViewModel() {
        super.initViewModel()
        commonViewModel.value.unFocusResult.observe(this) {
            dismissLoadingDialog()
            it?.apply {
                if (isSuccess) {
                    updateCount(0, false)
                    mFragments[mViewBinding.listVp.currentItem].finishUnFocused()
                }
            }
        }
    }


    override fun setData(savedInstanceState: Bundle?) {
        super.setData(savedInstanceState)
        initViews()
        mViewBinding.btnBack.setOnclick {
            finish()
        }
        mViewBinding.btnEdit.setOnclick {
            mIsSelectMode = !mIsSelectMode
            showHidFocusLayout()
        }
        mViewBinding.btnSubmit.setOnclick {
            val list = ArrayList<Int>()
            list.addAll(mFragments[mViewBinding.listVp.currentItem].getSelectedList())
            if (list.isNotEmpty()) {
                showLoadingDialog()
                commonViewModel.value.unFocus(list.getSplitString())
            } else {
                "请选择取消关注的内容".toast()
            }
        }
        setMultipleClick(mViewBinding.iconRadio, mViewBinding.textAll) {
            mFragments[mViewBinding.listVp.currentItem].selectAll(!mViewBinding.iconRadio.isSelected)
        }
        mViewBinding.listVp.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                mFragments[position].setSelectMode(mIsSelectMode)
                if (mFragments[position] is FragmentMyFocusArticle) {
                    updateCount(articleSelectedCount, isArticleSelectAll)
                } else {
                    updateCount(dealerSelectedCount, isDealerSelectAll)
                }
            }
        })
    }

    private var mIsSelectMode = false

    private fun showHidFocusLayout() {
        mFragments[mViewBinding.listVp.currentItem].setSelectMode(mIsSelectMode)
        if (mIsSelectMode) {
            mViewBinding.btnEdit.text = "退出编辑"
            mViewBinding.layoutBottom.visible()
            updateCount(0, false)
        } else {
            mViewBinding.btnEdit.text = "编辑"
            mViewBinding.layoutBottom.gone()
        }
    }

    private var articleSelectedCount = 0
    private var dealerSelectedCount = 0
    private var isArticleSelectAll = false
    private var isDealerSelectAll = false
    fun updateCount(count: Int, isAll: Boolean) {
        val curFragment = mFragments[mViewBinding.listVp.currentItem]
        mViewBinding.textCount.text = getString(
            if (curFragment is FragmentMyFocusDealer) {
                dealerSelectedCount = count
                isDealerSelectAll = isAll
                R.string.string_focus_dealer_count
            } else {
                articleSelectedCount = count
                isArticleSelectAll = isAll
                R.string.string_focus_article_count
            }, count
        )
        mViewBinding.iconRadio.isSelected = isAll
    }

    private fun initViews() {
        initFragments()
        mViewBinding.listVp.adapter = MyVpAdapter(this)
        initTab()
    }

    private fun initFragments() {
        mFragments.add(FragmentMyFocusDealer())
        mFragments.add(FragmentMyFocusArticle())
    }

    private fun initTab() {
        mViewBinding.listTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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
            mViewBinding.listTab, mViewBinding.listVp
        ) { tab, position ->
            run {
                when (position) {
                    0 -> {
                        tab.text = "交易商"
                    }

                    else -> {
                        tab.text = "资讯"
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