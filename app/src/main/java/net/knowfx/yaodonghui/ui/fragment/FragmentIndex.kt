package net.knowfx.yaodonghui.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.FragmentIndexBinding
import net.knowfx.yaodonghui.entities.*
import net.knowfx.yaodonghui.ext.falseLet
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ext.result
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ui.activity.BrokerSearchActivity
import net.knowfx.yaodonghui.ui.activity.MessageActivity
import net.knowfx.yaodonghui.ui.activity.WatchHistoryActivity
import net.knowfx.yaodonghui.ui.views.ListDividerItemDecoration
import net.knowfx.yaodonghui.utils.LayoutTypes
import net.knowfx.yaodonghui.viewModels.IndexViewModel

class FragmentIndex : Fragment() {
    companion object {
        const val POSITION_FUNCTION = 0
        const val POSITION_BANNER = 1
        const val POSITION_HISTORY_TITLE = 2
        const val POSITION_HISTORY_TOP = 3
        const val POSITION_HISTORY_BOTTOM = 4
        const val POSITION_PAGER = 5
    }

    private lateinit var mViewBinding: FragmentIndexBinding
    private lateinit var mViewModel: IndexViewModel
    private val mAdapter = CommonListAdapter<BaseListData> { _, data, _ ->
        when (data.layoutType) {
            LayoutTypes.TYPE_INDEX_HISTORY_TITLE -> {
                //点击历史查看更多
                jumpToTarget(requireActivity(), WatchHistoryActivity::class.java)
            }

            else -> {}
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mViewBinding = FragmentIndexBinding.inflate(inflater)
        initViews()
        initViewModel()
        requestData()
        return mViewBinding.root
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(requireActivity())[IndexViewModel::class.java]
        mViewModel.functions.observe(requireActivity()) {
            it?.apply {
                result(ArrayList<IndexFunctionListData.IndexFunctionData>(), error = {

                }, success = { data ->
                    val functionData = IndexFunctionListData()
                    functionData.indexFunctionList.addAll(data)
                    mAdapter.updateData(functionData, POSITION_FUNCTION)
                })
            }
        }

        mViewModel.banner.observe(requireActivity()) {
            it?.apply {
                result(ArrayList<IndexBannerListData.IndexBannerData>(), error = {

                }, success = { data ->
                    val bannerData = IndexBannerListData()
                    bannerData.bannerList.addAll(data)
                    mAdapter.updateData(bannerData, POSITION_BANNER)
                })
            }
        }
        mViewModel.hisTop.observe(requireActivity()) {
            it?.apply {
                result(ArrayList<IndexHistoryTopData.IndexHistoryTopItemData>(), error = {

                }, success = { data ->
                    val hisTopData = IndexHistoryTopData()
                    hisTopData.list.addAll(data)
                    mAdapter.updateData(hisTopData, POSITION_HISTORY_TOP)
                })
            }
        }
        mViewModel.hisBottom.observe(requireActivity()) {
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
                    val hisBottomData = IndexHistoryBottomListData()
                    hisBottomData.list.addAll(dataPage)
                    mAdapter.updateData(hisBottomData, POSITION_HISTORY_BOTTOM)
                })
            }
        }
    }

    private fun initViews() {
        mViewBinding.contentRv.addItemDecoration(ListDividerItemDecoration())
        mViewBinding.contentRv.layoutManager = LinearLayoutManager(requireContext())
        mViewBinding.contentRv.adapter = mAdapter
        val mDataList = ArrayList<BaseListData>()
        mDataList.add(POSITION_FUNCTION, IndexFunctionListData())
        mDataList.add(POSITION_BANNER, IndexBannerListData())
        mDataList.add(
            POSITION_HISTORY_TITLE,
            CommonTitleData(titleString = "浏览历史", isShowMore = true)
        )
        mDataList.add(POSITION_HISTORY_TOP, IndexHistoryTopData())
        mDataList.add(POSITION_HISTORY_BOTTOM, IndexHistoryBottomListData())
        mDataList.add(POSITION_PAGER, IndexPagerData())
        mAdapter.putData(mDataList)
        addListeners()
    }

    private fun requestData() {
        mViewModel.getFunctions()
        mViewModel.getBanner()
        mViewModel.getHisTop()
        mViewModel.getHisTBottom()
    }

    private fun addListeners() {
        mViewBinding.btnSearch.setOnclick {
            jumpToTarget(requireActivity(), BrokerSearchActivity::class.java)
        }
        mViewBinding.btnMessage.setOnclick {
            jumpToTarget(requireActivity(), MessageActivity::class.java)
        }
    }

}