package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.LayoutItemSuperviseFileBinding
import net.knowfx.yaodonghui.entities.SuperviseInfoData

class SuperviseFileHolder(parent: ViewGroup, layoutId: Int) : BaseViewHolder(parent, layoutId) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as SuperviseInfoData.FileData
        val binding = LayoutItemSuperviseFileBinding.bind(itemView)
        binding.textName.text = data.name
    }
}