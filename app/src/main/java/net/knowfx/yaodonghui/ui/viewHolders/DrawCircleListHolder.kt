package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.databinding.LayoutItemDrawListBinding
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.entities.DrawCircleListData
import net.knowfx.yaodonghui.ext.getCreateFormatTime
import net.knowfx.yaodonghui.ext.into
import net.knowfx.yaodonghui.ext.intoCircle
import net.knowfx.yaodonghui.ext.setOnclick

class DrawCircleListHolder(parent: ViewGroup, id: Int) : BaseViewHolder(parent, id) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as DrawCircleListData.Data
        val binding = LayoutItemDrawListBinding.bind(itemView)
        binding.titleTv.text = data.title
        binding.timeTv.text = data.createTime.getCreateFormatTime()
        binding.avatarIv.intoCircle(data.userhead)
        binding.thumbIv.into(data.coverPicture)
        binding.nicknameTv.text = data.nickname
        itemView.setOnclick {
            onItemClicked?.invoke(it, data, position)
        }
    }
}