package net.knowfx.yaodonghui.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.base.BaseRefreshFragment
import net.knowfx.yaodonghui.databinding.FragmentNewIndexBinding
import net.knowfx.yaodonghui.entities.IndexBannerListData
import net.knowfx.yaodonghui.entities.IndexFunctionListData
import net.knowfx.yaodonghui.entities.IndexHistoryBottomListData
import net.knowfx.yaodonghui.entities.IndexHistoryTopData
import net.knowfx.yaodonghui.ext.bindController
import net.knowfx.yaodonghui.ext.checkIsLogin
import net.knowfx.yaodonghui.ext.falseLet
import net.knowfx.yaodonghui.ext.getToken
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.indexFunctionJump
import net.knowfx.yaodonghui.ext.jumpFromPush
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ext.unBindController
import net.knowfx.yaodonghui.ext.visible
import net.knowfx.yaodonghui.ui.activity.BrokerContentActivity
import net.knowfx.yaodonghui.ui.activity.BrokerSearchActivity
import net.knowfx.yaodonghui.ui.activity.MessageActivity
import net.knowfx.yaodonghui.ui.activity.WatchHistoryActivity
import net.knowfx.yaodonghui.ui.activity.WebActivity
import net.knowfx.yaodonghui.utils.CommonMarginDecoration
import net.knowfx.yaodonghui.viewModels.IndexViewModel

class FragmentNewIndex : Fragment() {
    private val mBinding = lazy { FragmentNewIndexBinding.inflate(layoutInflater) }
    private val mViewModel =
        lazy { ViewModelProvider(requireActivity())[IndexViewModel::class.java] }


    private val mFunctionAdapter =
        CommonListAdapter<IndexFunctionListData.IndexFunctionData> { _, data, _ ->
            data as IndexFunctionListData.IndexFunctionData
            (requireActivity() as BaseActivity).indexFunctionJump(data)
        }

    private val mHistoryMiddleAdapter =
        CommonListAdapter<IndexHistoryTopData.IndexHistoryTopItemData> { _, data, _ ->
            data as IndexHistoryTopData.IndexHistoryTopItemData
            val params = HashMap<String, Any>()
            params["brokerId"] = data.id
            jumpToTarget(
                requireActivity() as BaseActivity,
                BrokerContentActivity::class.java,
                params
            )
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initObserver()
        initViews()
        addListeners()
        return mBinding.value.root
    }

    private fun initObserver() {
        mViewModel.value.functions.observe(requireActivity()) {
            mBinding.value.fullRefresh.finishRefresh()
            it?.apply {
                result(ArrayList<IndexFunctionListData.IndexFunctionData>(), error = {},
                    success = { data ->
                        mFunctionAdapter.putData(data)
                        resetRvHeight(
                            mBinding.value.functionRv,
                            resources.getDimensionPixelOffset(R.dimen.dp_85),
                            if (data.size % 5 == 0) {
                                data.size / 5
                            } else {
                                (data.size / 5) + 1
                            }
                        )
                    })
            }
        }

        mViewModel.value.banner.observe(requireActivity()) {
            mBinding.value.fullRefresh.finishRefresh()
            it?.apply {
                result(ArrayList<IndexBannerListData.IndexBannerData>(), error = {

                }, success = { data ->
                    //设置banner
                    setupBanner(data)
                })
            }
        }
        mViewModel.value.hisTop.observe(requireActivity()) {
            mBinding.value.fullRefresh.finishRefresh()
            it?.apply {
                result(ArrayList<IndexHistoryTopData.IndexHistoryTopItemData>(), error = {

                }, success = { data ->
                    mHistoryMiddleAdapter.putData(data)
                    resetRvHeight(
                        mBinding.value.historyMiddleRv,
                        resources.getDimensionPixelOffset(R.dimen.dp_60),
                        if (data.size % 4 == 0) {
                            data.size / 4
                        } else {
                            (data.size / 4) + 1
                        }
                    )
                })
            }
        }
        mViewModel.value.hisBottom.observe(requireActivity()) {
            mBinding.value.fullRefresh.finishRefresh()
            it?.apply {
                result(ArrayList<IndexHistoryBottomListData.HistoryBottomItemData>(), error = {

                }, success = { data ->
                    val dataPage =
                        ArrayList<ArrayList<IndexHistoryBottomListData.HistoryBottomItemData>>()
                    var pageList = ArrayList<IndexHistoryBottomListData.HistoryBottomItemData>()
                    for (i in 0 until data.size) {
                        if (i % 9 == 0) {
                            (i == 0).falseLet {
                                dataPage.add(pageList)
                            }
                            pageList = ArrayList()
                            pageList.add(data[i])
                        } else {
                            pageList.add(data[i])
                            (i == data.size - 1).trueLet {
                                dataPage.add(pageList)
                            }
                        }
                    }
                    //设置历史下部Viewpager
                    setupHistoryBottom(dataPage)
                })
            }
        }

        mViewModel.value.unreadResult.observe(this) {
            it?.apply {
                result(UnreadData(), error = {}, success = { data ->
                    if (data.allNoReadyCount > 0) {
                        mBinding.value.redPoint.visible()
                    } else {
                        mBinding.value.redPoint.gone()
                    }
                })
            }
        }
    }

    data class UnreadData(
        val allNoReadyCount: Int = 0
    )

    private fun initViews() {
        setupFunctionView()
        setupHistoryMiddle()
        setupVp()
        mBinding.value.fullRefresh.bindController(true) {
            requestData(true)
        }
        requestData(false)
    }

    private fun addListeners() {
        mBinding.value.btnSearch.setOnclick {
            jumpToTarget(
                requireActivity(),
                BrokerSearchActivity::class.java,
                hashMapOf(Pair("isSelect", false))
            )
        }
        mBinding.value.btnMessage.setOnclick {
            requireActivity().checkIsLogin {
                jumpToTarget(requireActivity(), MessageActivity::class.java)
            }
        }
        mBinding.value.layoutHisTitle.btnLookMore.setOnclick {
            jumpToTarget(requireActivity(), WatchHistoryActivity::class.java)
        }
        mBinding.value.scroll.setOnScrollChangeListener { v, _, scrollY, _, _ ->
            if (scrollY > mBinding.value.pagerTab.top) {
                v.scrollY = mBinding.value.pagerTab.top
                mFragments.forEach {
                    it.setCanScroll(true)
                }
            } else {
                mFragments.forEach {
                    it.setCanScroll(false)
                }
            }
        }
    }

    private fun requestData(isFull: Boolean) {
        mViewModel.value.getFunctions()
        mViewModel.value.getBanner()
        mViewModel.value.getHisTop()
        mViewModel.value.getHisTBottom()
        if (isFull) {
            mFragments.forEach {
                it.refresh()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (getToken().isNotEmpty()) {
            mViewModel.value.getUnread()
        }
    }

    private fun setupFunctionView() {
        mBinding.value.functionRv.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            val offsetV = resources.getDimensionPixelOffset(R.dimen.dp_18)
            addItemDecoration(CommonMarginDecoration(offsetV, 0, 5, false))
            layoutManager = object : GridLayoutManager(requireContext(), 5) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            adapter = mFunctionAdapter
        }
    }

    private var mCurBannerPosition = 0

    private fun setupBanner(list: ArrayList<IndexBannerListData.IndexBannerData>) {
        initBannerIndex(list)
        mBinding.value.layoutBanner.viewBanner.apply {
            val width =
                resources.displayMetrics.widthPixels - (2 * resources.getDimensionPixelOffset(
                    R.dimen.dp_12
                ))
            val height = width * 3 / 7
            val params =
                layoutParams ?: ConstraintLayout.LayoutParams(width, height)
            params.width = width
            params.height = height
            layoutParams = params
            addIndexChangeListener {
                switchIndex(it)
            }
            initBanner(list)
            addClickListener {
                //跳转
                if (it.seekId.isNotEmpty() && it.seekId != "null" && it.model.isNotEmpty() && it.model != "null") {
                    (requireActivity() as BaseActivity).jumpFromPush(it.model, it.seekId.toInt())
                } else {
                    jumpToTarget(
                        requireActivity(),
                        WebActivity::class.java,
                        hashMapOf(
                            Pair("url", it.url),
                            Pair("title", it.title)
                        )
                    )
                }
            }
            switchIndex(mCurBannerPosition)
        }
    }

    private fun switchIndex(position: Int) {
        mBinding.value.layoutBanner.layoutIndex.apply {
            getChildAt(mCurBannerPosition).isSelected = false
            getChildAt(position).isSelected = true
            mCurBannerPosition = position
        }
    }

    private fun initBannerIndex(list: ArrayList<IndexBannerListData.IndexBannerData>) {
        mBinding.value.layoutBanner.apply {
            layoutIndex.removeAllViews()
            val width = resources.getDimensionPixelOffset(R.dimen.dp_20)
            val height = resources.getDimensionPixelOffset(R.dimen.dp_5)
            val distance = resources.getDimensionPixelOffset(R.dimen.dp_2)
            when (list.size) {
                0, 1 -> {
                    val view = View(requireContext())
                    val params = LinearLayout.LayoutParams(width, height)
                    view.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_banner_index_middle
                    )
                    view.layoutParams = params
                    layoutIndex.addView(view)
                }

                2 -> {
                    val viewLeft = View(requireContext())
                    val paramsL = LinearLayout.LayoutParams(width, height)
                    paramsL.rightMargin = distance
                    viewLeft.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_banner_index_first
                    )
                    viewLeft.layoutParams = paramsL
                    layoutIndex.addView(viewLeft)

                    val viewRight = View(requireContext())
                    val paramsR = LinearLayout.LayoutParams(width, height)
                    viewRight.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_banner_index_last
                    )
                    viewRight.layoutParams = paramsR
                    layoutIndex.addView(viewRight)
                }

                else -> {
                    for (i in 0 until list.size) {
                        val view = View(requireContext())
                        val params = LinearLayout.LayoutParams(width, height)
                        when (i) {
                            0 -> {
                                params.rightMargin = distance
                                view.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_banner_index_first
                                )
                            }

                            list.size - 1 -> {
                                view.background =
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.bg_banner_index_last
                                    )
                            }

                            else -> {
                                params.rightMargin = distance
                                view.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_banner_index_middle
                                )
                            }
                        }
                        view.layoutParams = params
                        layoutIndex.addView(view)
                    }
                }
            }
        }
    }

    private fun setupHistoryMiddle() {
        mBinding.value.historyMiddleRv.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            val offset = resources.getDimensionPixelOffset(R.dimen.dp_5)
            addItemDecoration(CommonMarginDecoration(offset, offset, 4, false))
            layoutManager = object : GridLayoutManager(requireContext(), 4) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            adapter = mHistoryMiddleAdapter
        }
    }

    private fun setupHistoryBottom(list: ArrayList<ArrayList<IndexHistoryBottomListData.HistoryBottomItemData>>) {
        val hisBottomFragments = ArrayList<Fragment>()
        mBinding.value.layoutHisBottom.apply {
            val layoutParams = itemVp.layoutParams
            layoutParams.height = 3 * resources.getDimensionPixelOffset(R.dimen.dp_40)
            itemVp.layoutParams = layoutParams
            list.isNotEmpty().trueLet {
                list.forEach {
                    hisBottomFragments.add(FragmentIndexHistoryBottomVp(it))
                }
                initHisBottomIndex(hisBottomFragments)
                hisBottomFragments.add(0, FragmentIndexHistoryBottomVp(list.last()))
                hisBottomFragments.add(FragmentIndexHistoryBottomVp(list.first()))
                itemVp.addIndexChangeListener {
                    switchHisBottomIndex(it)
                }
                itemVp.initCustom(fragments = hisBottomFragments)
                switchHisBottomIndex(mHisBottomPosition)
            }
        }
    }

    private fun initHisBottomIndex(hisBottomFragments: ArrayList<Fragment>) {
        mBinding.value.layoutHisBottom.apply {
            itemIndex.removeAllViews()
            repeat(hisBottomFragments.size) {
                val view = View(requireContext())
                val size = requireContext().resources.getDimensionPixelSize(R.dimen.dp_5)
                val params = LinearLayoutCompat.LayoutParams(size, size)
                view.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.icon_banner_index_default
                    )
                (it > 0).trueLet {
                    params.leftMargin = resources.getDimensionPixelOffset(R.dimen.dp_4)
                }
                view.layoutParams = params
                itemIndex.addView(view)
            }
        }
    }

    private var mHisBottomPosition = 0

    private fun switchHisBottomIndex(currentIndex: Int) {
        mBinding.value.layoutHisBottom.apply {
            itemIndex.getChildAt(mHisBottomPosition)?.isSelected = false
            itemIndex.getChildAt(currentIndex)?.isSelected = true
            mHisBottomPosition = currentIndex
        }
    }

    private val mFragments = arrayListOf<BaseRefreshFragment>(
        FragmentIndexVpList(FragmentIndexVpList.CLASS_ID_NEWEST),
        FragmentIndexVpList(FragmentIndexVpList.CLASS_ID_ORIGINAL),
        FragmentIndexVpList(FragmentIndexVpList.CLASS_ID_BROKER)
    )

    private fun setupVp() {
        mBinding.value.apply {
            val par = pagerVp.layoutParams
            par.height =
                resources.displayMetrics.heightPixels - mBinding.value.pagerTab.bottom - resources.getDimensionPixelOffset(
                    R.dimen.dp_55
                )
            pagerVp.layoutParams = par
            pagerVp.offscreenPageLimit = 2
            pagerVp.adapter = MyVpAdapter(requireActivity() as BaseActivity)
            pagerVp.currentItem = 0
            val tabLayoutMediator = TabLayoutMediator(
                pagerTab, pagerVp
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
    }

    private fun resetRvHeight(view: RecyclerView, itemHeight: Int, count: Int) {
        val param = view.layoutParams
        param.height = itemHeight * count
        view.layoutParams = param
    }

    override fun onDestroy() {
        mBinding.value.fullRefresh.unBindController()
        super.onDestroy()
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

}