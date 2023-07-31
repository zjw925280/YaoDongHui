package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.LayoutItemExploreListBinding
import net.knowfx.yaodonghui.entities.ExploreListData
import net.knowfx.yaodonghui.ext.getCreateFormatTime
import net.knowfx.yaodonghui.ext.intoCorners
import net.knowfx.yaodonghui.ext.intoLogoOrCover
import net.knowfx.yaodonghui.ext.setOnclick

class ExploreListHolder(parent: ViewGroup, resId: Int) : BaseViewHolder(parent, resId) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as ExploreListData.Data
        val binding = LayoutItemExploreListBinding.bind(itemView)
        binding.labelTv.text = data.typeName
        binding.contentTv.text = data.content
        binding.titleTv.text = data.title
        val corner = itemView.resources.getDimensionPixelOffset(R.dimen.dp_5)
        val height = itemView.resources.getDimensionPixelOffset(R.dimen.dp_80)
        binding.thumbIv.intoLogoOrCover(data.exposurefile, height, corner)
        binding.logoIv.intoCorners(
            data.logo,
            itemView.resources.getDimensionPixelOffset(R.dimen.dp_2).toFloat()
        )
        binding.nameTv.text = data.dealerName
        binding.timeTv.text = data.createTime.getCreateFormatTime()
        itemView.setOnclick {
            onItemClicked?.invoke(it, data, position)
        }
    }
}