package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.adapters.CommonListAdapter
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.LayoutIndexHistoryTopBinding
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.entities.IndexHistoryTopData
import net.knowfx.yaodonghui.ext.jumpToTarget
import net.knowfx.yaodonghui.ui.activity.BrokerContentActivity
import net.knowfx.yaodonghui.utils.CommonMarginDecoration

class IndexHistoryTopListHolder(parent: ViewGroup, id: Int) : BaseViewHolder(parent, id) {
    private val mAdapter =
        CommonListAdapter<IndexHistoryTopData.IndexHistoryTopItemData> { _, data, _ ->
            data as IndexHistoryTopData.IndexHistoryTopItemData
            val params = HashMap<String, Any>()
            params["brokerId"] = data.id
            jumpToTarget(
                itemView.context as BaseActivity,
                BrokerContentActivity::class.java,
                params
            )
        }

    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        val binding = LayoutIndexHistoryTopBinding.bind(itemView)
        data as IndexHistoryTopData
        binding.root.setHasFixedSize(true)
        binding.root.isNestedScrollingEnabled = false
        val offset = itemView.resources.getDimensionPixelOffset(R.dimen.dp_5)
        binding.root.addItemDecoration(CommonMarginDecoration(offset, offset, 4, false))
        binding.root.layoutManager = object : GridLayoutManager(itemView.context, 4) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        binding.root.adapter = mAdapter
        mAdapter.putData(dataList = data.list)
    }
}