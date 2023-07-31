package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.LayoutItemSearchHotBinding
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.entities.SearchHotData
import net.knowfx.yaodonghui.ext.intoCorners
import net.knowfx.yaodonghui.ext.setOnclick

class SearchHotHolder(parent: ViewGroup, id: Int) : BaseViewHolder(parent, id) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as SearchHotData
        val binding = LayoutItemSearchHotBinding.bind(itemView)
        binding.hotIcon.intoCorners(
            data.logo,
            itemView.resources.getDimension(R.dimen.dp_5)
        )
        binding.hotName.text = data.name
        itemView.setOnclick {
            onItemClicked?.invoke(it, data, position)
        }
    }
}