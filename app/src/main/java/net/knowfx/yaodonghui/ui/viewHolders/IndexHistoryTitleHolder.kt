package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.databinding.LayoutItemIndexHistoryTitleBinding
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.entities.CommonTitleData
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.trueLet
import net.knowfx.yaodonghui.ext.visible

class IndexHistoryTitleHolder(parent: ViewGroup, id: Int) : BaseViewHolder(parent, id) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as CommonTitleData
        val binding = LayoutItemIndexHistoryTitleBinding.bind(itemView)
        data.isShowMore.trueLet {
            binding.btnLookMore.visible()
            binding.btnLookMore.setOnclick {
                onItemClicked?.invoke(it, data, position)
            }
        }.elseLet {
            binding.btnLookMore.gone()
        }
    }
}