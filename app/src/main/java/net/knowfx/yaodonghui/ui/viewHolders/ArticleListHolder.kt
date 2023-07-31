package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.LayoutItemCommonArticleBinding
import net.knowfx.yaodonghui.entities.ArticleListData
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.ext.getCreateFormatTime
import net.knowfx.yaodonghui.ext.intoCircle
import net.knowfx.yaodonghui.ext.intoCorners
import net.knowfx.yaodonghui.ext.intoLogoOrCover
import net.knowfx.yaodonghui.ext.setOnclick

class ArticleListHolder(parent: ViewGroup, id: Int) : BaseViewHolder(parent, id) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as ArticleListData.ListData
        val binding = LayoutItemCommonArticleBinding.bind(itemView)
        val height = itemView.resources.getDimensionPixelOffset(R.dimen.dp_80)
        val corner = itemView.resources.getDimensionPixelOffset(R.dimen.dp_5)
        binding.thumbIv.intoLogoOrCover(data.coverPicture, height = height, corner = corner)
        binding.titleTv.text = data.title
        binding.avatarIv.intoCircle(data.userhead)
        binding.nameTv.text = data.nickname
        binding.timeTv.text = data.createTime.getCreateFormatTime()
        itemView.setOnclick {
            onItemClicked?.invoke(it, data, position)
        }
    }
}