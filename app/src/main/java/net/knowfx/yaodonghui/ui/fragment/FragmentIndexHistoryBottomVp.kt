package net.knowfx.yaodonghui.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.databinding.FragmentIndexHistoryBottomBinding
import net.knowfx.yaodonghui.entities.IndexHistoryBottomListData
import net.knowfx.yaodonghui.ext.*
import net.knowfx.yaodonghui.ui.activity.BrokerContentActivity
import net.knowfx.yaodonghui.utils.CommonMarginDecoration

class FragmentIndexHistoryBottomVp : Fragment {
    private lateinit var mBinding: FragmentIndexHistoryBottomBinding
    private val mDataList = ArrayList<IndexHistoryBottomListData.HistoryBottomItemData>()
    private val mAdapter =
        CommonListAdapter<IndexHistoryBottomListData.HistoryBottomItemData> { _, data, _ ->
            data as IndexHistoryBottomListData.HistoryBottomItemData
            val params = HashMap<String, Any>()
            params["brokerId"] = data.id
            jumpToTarget(
                requireActivity(),
                BrokerContentActivity::class.java,
                params
            )
        }

    constructor() : super()

    constructor(dataList: ArrayList<IndexHistoryBottomListData.HistoryBottomItemData>) : super() {
        mDataList.addAll(dataList)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentIndexHistoryBottomBinding.inflate(inflater)
        initView()
        return mBinding.root
    }

    private fun initView(){
        mBinding.root.setHasFixedSize(true)
        mBinding.root.isNestedScrollingEnabled = false
        val offset = resources.getDimensionPixelOffset(R.dimen.dp_17)
        mBinding.root.addItemDecoration(CommonMarginDecoration(0, offset, 3, false))
        mBinding.root.layoutManager = object : GridLayoutManager(context, 3) {
            override fun canScrollVertically(): Boolean {
                return false
            }

            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        mBinding.root.adapter = mAdapter
        mAdapter.putData(dataList = mDataList)
    }
}