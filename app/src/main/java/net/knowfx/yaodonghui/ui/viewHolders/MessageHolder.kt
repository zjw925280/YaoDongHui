package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.LayoutItemMessageBinding
import net.knowfx.yaodonghui.entities.MessageData
import net.knowfx.yaodonghui.ext.formatTime
import kotlin.math.max

class MessageHolder(parent: ViewGroup, layoutId: Int) : BaseViewHolder(parent, layoutId) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        val binding = LayoutItemMessageBinding.bind(itemView)
        (data as MessageData).apply {
            binding.textTime.text = max(create_time, createTime).formatTime("yyyy-MM-dd    HH:mm")
            binding.textTitle.text = title
            binding.textContent.text = content
        }
    }
}