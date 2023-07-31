package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.LayoutItemIndexHistoryBottomBinding
import net.knowfx.yaodonghui.entities.IndexHistoryBottomListData
import net.knowfx.yaodonghui.ext.intoCorners
import net.knowfx.yaodonghui.ext.setOnclick

class IndexHistoryBottomItemHolder(parent: ViewGroup, resId: Int) : BaseViewHolder(parent, resId) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as IndexHistoryBottomListData.HistoryBottomItemData
        val binding = LayoutItemIndexHistoryBottomBinding.bind(itemView)
        val corner = itemView.resources.getDimension(R.dimen.dp_4)
        binding.copLogo.intoCorners(data.logo, corner)
        binding.copName.text = data.fullName
        itemView.setOnclick {
            onItemClicked?.invoke(it, data, position)
        }
    }

}