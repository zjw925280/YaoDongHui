package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.LayoutNoDataBinding
import net.knowfx.yaodonghui.entities.EmptyData

class NoDataHolder(parent: ViewGroup, layoutId: Int) : BaseViewHolder(parent, layoutId) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as EmptyData
        val binding = LayoutNoDataBinding.bind(itemView)
        binding.noDataIv.setImageLevel(data.type)
        binding.noDataTv.text = data.getWarningText(itemView.resources)
    }
}