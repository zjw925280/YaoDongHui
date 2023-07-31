package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.LayoutItemSuperviseNewsBinding
import net.knowfx.yaodonghui.entities.IndexPagerData
import net.knowfx.yaodonghui.ext.getCreateFormatTime
import net.knowfx.yaodonghui.ext.getTextFromModel
import net.knowfx.yaodonghui.ext.intoCorners
import net.knowfx.yaodonghui.ext.intoLogoOrCover
import net.knowfx.yaodonghui.ext.setOnclick

class IndexPagerListHolder(parent: ViewGroup, resId: Int) : BaseViewHolder(parent, resId) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as IndexPagerData.ListData
        val binding = LayoutItemSuperviseNewsBinding.bind(itemView)
        val corners = itemView.resources.getDimensionPixelOffset(R.dimen.dp_5)
        val height = itemView.resources.getDimensionPixelOffset(R.dimen.dp_80)
        binding.thumbIv.intoLogoOrCover(data.coverPicture, height, corners)
        val labelStr = getTextFromModel(data.model)
        val blanks = when (labelStr.length) {
            3 -> "\t\t\t\t\t\t\t"
            4 -> "\t\t\t\t\t\t\t\t"
            5 -> "\t\t\t\t\t\t\t\t\t"
            else -> "\t\t\t"
        }
        binding.titleTv.text = itemView.resources.getString(
            R.string.string_text_supervise_news_title_pattern,
            blanks,
            data.title
        )
        binding.nameTimeTv.text = itemView.resources.getString(
            R.string.string_text_supervise_news_name_time,
            data.nickname,
            data.createTime.getCreateFormatTime()
        )
        binding.labelTv.text = getTextFromModel(data.model)
        itemView.setOnclick {
            onItemClicked?.invoke(it, data, position)
        }
    }
}