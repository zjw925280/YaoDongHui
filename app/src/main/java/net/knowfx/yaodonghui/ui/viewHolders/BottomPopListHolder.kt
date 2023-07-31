package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.LayoutItemDialogListBinding
import net.knowfx.yaodonghui.entities.BottomPopData
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.into
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.visible

class BottomPopListHolder(parent: ViewGroup, resId: Int) : BaseViewHolder(parent, resId) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as BottomPopData
        val binding = LayoutItemDialogListBinding.bind(itemView)
        binding.icon.apply {
            if (data.iconId == 0) {
                gone()
            } else {
                visible()
                binding.icon.into(data.iconId)
            }
        }
        binding.text.text = data.text
        binding.root.setOnclick {
            onItemClicked?.invoke(it, data, position)
        }
    }
}