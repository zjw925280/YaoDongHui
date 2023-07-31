package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.LayoutItemMyPostBinding
import net.knowfx.yaodonghui.entities.MyExploreData
import net.knowfx.yaodonghui.ext.getCreateFormatTime
import net.knowfx.yaodonghui.ext.intoCorners
import net.knowfx.yaodonghui.ext.intoLogoOrCover
import net.knowfx.yaodonghui.ext.setOnclick

class MyExploreHolder(parent: ViewGroup, layoutId: Int) : BaseViewHolder(parent, layoutId) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as MyExploreData.Data
        val binding = LayoutItemMyPostBinding.bind(itemView)
        val corner = itemView.resources.getDimensionPixelOffset(R.dimen.dp_5)
        val height = itemView.resources.getDimensionPixelOffset(R.dimen.dp_80)
        binding.thumbIv.intoLogoOrCover(url = data.exposurefile, height = height, corner = corner)
        binding.titleTv.text = data.title
        binding.contentTv.text = data.content
        binding.labelTv.text = data.typeName
        binding.textDealer.text =
            itemView.context.getString(R.string.string_my_post_dealer, data.name)
        binding.textTime.text = data.createTime.getCreateFormatTime()
        itemView.setOnclick {
            onItemClicked?.invoke(it, data, position)
        }
    }

}