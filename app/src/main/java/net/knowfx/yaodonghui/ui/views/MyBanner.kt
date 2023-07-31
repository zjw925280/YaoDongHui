package net.knowfx.yaodonghui.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.LayoutBannerBinding
import net.knowfx.yaodonghui.entities.IndexBannerListData
import net.knowfx.yaodonghui.ext.falseLet
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ext.visible
import net.knowfx.yaodonghui.ui.fragment.FragmentBannerItem

class MyBanner @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    /**Banner数据对象集合*/
    private val mDataList = ArrayList<IndexBannerListData.IndexBannerData>()

    /**BannerViewPager的Fragment集合*/
    private val mFragments = ArrayList<Fragment>()

    /**指示器View集合*/
    private val mIndexViews = ArrayList<View>()

    /**界面的ViewBinding*/
    private lateinit var mBinding: LayoutBannerBinding

    /**ViewPager的数据适配器*/
    private val mPagerAdapter = MyVpAdapter(context as BaseActivity)

    /**当前ViewPager滑动的位置*/
    private var mCurrentPosition = 1

    /**滚动倒计时定时器*/
    private val mCountDownTimer = MyCountDown(2000L, 100L)

    private val mModifiedList = ArrayList<IndexBannerListData.IndexBannerData>()

    private var onIndexChangeListener: ((position: Int) -> Unit)? = null
    private var onClickListener: ((data: IndexBannerListData.IndexBannerData) -> Unit)? = null
    override fun onFinishInflate() {
        //初始化界面ViewBinding并添加进当前界面
        mBinding = LayoutBannerBinding.inflate(LayoutInflater.from(context))
        addView(mBinding.root)
        super.onFinishInflate()
    }

    /**
     * 使用数据集合 dataList 和 指示器View集合 list 初始化Banner，此时使用自定义的指示器View集合
     */
    fun initBanner(
        dataList: ArrayList<IndexBannerListData.IndexBannerData>,
        list: ArrayList<View>? = null
    ) {
        mDataList.clear()
        mModifiedList.clear()
        mFragments.clear()
        mCurrentPosition = 1
        mCountDownTimer.cancelCount()
        setBanner(dataList, list)
    }

    fun initCustom(fragments: ArrayList<Fragment>) {
        mCountDownTimer.cancelCount()
        mFragments.isNotEmpty().trueLet {
            mFragments.clear()
        }
        mFragments.addAll(fragments)
        initViewPager()
    }

    /**
     * 设置banner
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setBanner(
        dataList: ArrayList<IndexBannerListData.IndexBannerData>,
        list: ArrayList<View>? = null
    ) {
        //添加并处理数据集合
        mDataList.addAll(dataList)
        mModifiedList.addAll(dataList)
        (mDataList.size > 0).trueLet {
            mModifiedList.add(0, dataList.last())
            mModifiedList.add(dataList.first())

            //创建ViewPager内部的Fragment集合
            mModifiedList.forEach {
                val fragment = FragmentBannerItem()
                fragment.setPicPath(it, context.resources.getDimension(R.dimen.dp_10)) { data ->
                    onClickListener?.invoke(data)
                }
                mFragments.add(fragment)
            }
            if (list == null) {
                mBinding.bannerIndexLayout.gone()
            } else {
                mBinding.bannerIndexLayout.visible()
                addIndexViews(list.filterIsInstanceTo(ArrayList()))
            }
            initViewPager()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initViewPager() {
        //设置数据适配器
        mBinding.bannerSwitch.adapter = mPagerAdapter
        mBinding.bannerSwitch.setCurrentItem(mCurrentPosition, false)
        mBinding.bannerSwitch.offscreenPageLimit = 1
        //开始滚动倒计时
        mCountDownTimer.startCount()
        //注册ViewPager页面改变的回调
        mBinding.bannerSwitch.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (mFragments.size <= 3) return
                mCurrentPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                (state != ViewPager2.SCROLL_STATE_IDLE).trueLet {
                    return
                }/*
                * 通过ViewPager的位置设置下标指示器的选中位置
                * 如果滑动到第一页，则改变至数据集合最后一位
                * 如果滑动到最后一页，则改变至数据集合第一位
                */
                changeIndex(
                    when (mCurrentPosition) {
                        0 -> {
                            mFragments.size - 3
                        }

                        mPagerAdapter.itemCount - 1 -> {
                            0
                        }

                        else -> {
                            mCurrentPosition - 1
                        }
                    }
                )
                //再次开始滚动倒计时
                mCountDownTimer.startCount()
            }
        })
        mBinding.touchView.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                //用户触摸，则停止自动滚动的定时器
                mCountDownTimer.cancelCount()
            }
            false
        }
    }

    /**
     * 使用用户自定义的下标指示器
     */
    private fun addIndexViews(list: ArrayList<View>) {
        (mIndexViews.isNotEmpty()).trueLet {
            mBinding.bannerIndexLayout.removeAllViews()
            mIndexViews.clear()
        }
        mIndexViews.addAll(list)
        mIndexViews.forEach {
            mBinding.bannerIndexLayout.addView(it)
        }
        mIndexViews[0].isSelected = true
    }

    /**
     * 根据ViewPager页码 page 切换下标指示器
     */
    private fun changeIndex(page: Int) {
        if (mCurrentPosition != page + 1) {
            //如果ViewPager当前item的下标与指示器下标不匹配，则重定位ViewPager
            mCurrentPosition = page + 1
            mBinding.bannerSwitch.setCurrentItem(mCurrentPosition, false)
        }
        (mBinding.bannerIndexLayout.isNotEmpty()).trueLet {
            //设置指示器的选中状态
            mBinding.bannerIndexLayout.children.forEach {
                if (it.isSelected) {
                    it.isSelected = false
                }
            }
            mBinding.bannerIndexLayout.getChildAt(page).isSelected = true
        }.elseLet {
            onIndexChangeListener?.invoke(page)
        }
    }

    fun addClickListener(listener: (data: IndexBannerListData.IndexBannerData) -> Unit) {
        onClickListener = listener
    }

    fun addIndexChangeListener(listener: (position: Int) -> Unit) {
        onIndexChangeListener = listener
    }

    /**
     * ViewPager数据适配器
     */
    inner class MyVpAdapter(fgt: BaseActivity) : FragmentStateAdapter(fgt) {
        override fun getItemCount(): Int {
            return mFragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return mFragments[position]
        }
    }

    /**
     * 自动滚动的定时器
     */
    inner class MyCountDown(timeTotal: Long, tickDuration: Long) :
        CountDownTimer(timeTotal, tickDuration) {
        private var isCounting = false
        override fun onTick(remain: Long) {
            //不用实现
        }

        override fun onFinish() {
            isCounting = false
            mBinding.bannerSwitch.post {
                mBinding.bannerSwitch.currentItem = mCurrentPosition + 1
            }
        }

        /**开始倒计时的方法*/
        fun startCount() {
            isCounting.falseLet {
                isCounting = true
                start()
            }
        }

        /**停止倒计时的方法*/
        fun cancelCount() {
            isCounting.trueLet {
                isCounting = false
                cancel()
            }
        }
    }
}