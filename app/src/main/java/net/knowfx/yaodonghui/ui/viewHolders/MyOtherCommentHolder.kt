package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.LayoutItemMyOtherCommentBinding
import net.knowfx.yaodonghui.entities.MyOtherCommentData
import net.knowfx.yaodonghui.ext.formatTime
import net.knowfx.yaodonghui.ext.getCreateFormatTime
import net.knowfx.yaodonghui.ext.getTextFromModel
import net.knowfx.yaodonghui.ext.intoCorners
import net.knowfx.yaodonghui.ext.intoLogoOrCover
import net.knowfx.yaodonghui.ext.setOnclick

class MyOtherCommentHolder(parent: ViewGroup, layoutId: Int) : BaseViewHolder(parent, layoutId) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as MyOtherCommentData.Data
        val binding = LayoutItemMyOtherCommentBinding.bind(itemView)
        binding.tvTime.text = data.createTime.formatTime()
        binding.tvContent.text = data.content
        val corners = itemView.resources.getDimensionPixelOffset(R.dimen.dp_5)
        val height = itemView.resources.getDimensionPixelOffset(R.dimen.dp_40)
        binding.thumbIv.intoLogoOrCover(
            data.coverPicture,
            height = height,
            corner = corners
        )
        binding.labelTv.text = getTextFromModel(data.model)
        binding.titleTv.text = data.title
        binding.nameTimeTv.text =
            "${data.createUser}${itemView.resources.getString(R.string.string_divide)}${data.createTime.getCreateFormatTime()}"
        itemView.setOnclick { onItemClicked?.invoke(it, data, position) }
    }
}