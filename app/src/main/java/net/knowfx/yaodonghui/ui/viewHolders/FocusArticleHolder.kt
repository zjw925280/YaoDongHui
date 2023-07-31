package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.LayoutItemFocusArticleBinding
import net.knowfx.yaodonghui.entities.FocusArticleData
import net.knowfx.yaodonghui.ext.getCreateFormatTime
import net.knowfx.yaodonghui.ext.getTextFromModel
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.intoLogoOrCover
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.visible

class FocusArticleHolder(parent: ViewGroup, layoutId: Int) : BaseViewHolder(parent, layoutId) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as FocusArticleData.ItemData
        val binding = LayoutItemFocusArticleBinding.bind(itemView)
        binding.layoutArticle.apply {
            val height = itemView.resources.getDimensionPixelOffset(R.dimen.dp_80)
            val corner = itemView.resources.getDimensionPixelOffset(R.dimen.dp_4)
            thumbIv.intoLogoOrCover(data.logofile, height, corner)
            val labelStr = getTextFromModel(data.model)
            labelTv.text = labelStr
            val blanks = when (labelStr.length) {
                3 -> "\t\t\t\t\t\t"
                4 -> "\t\t\t\t\t\t\t"
                5 -> "\t\t\t\t\t\t\t\t"
                else -> "\t\t\t"
            }
            titleTv.text = itemView.resources.getString(
                R.string.string_text_supervise_news_title_pattern,
                blanks,
                data.title
            )
            nameTimeTv.text = itemView.resources.getString(
                R.string.string_text_supervise_news_name_time,
                data.nickname,
                data.createTime.getCreateFormatTime()
            )
        }
        if (data.isInSelectMode) {
            binding.iconCheck.visible()
            binding.iconCheck.isSelected = data.isSelected
        } else {
            binding.iconCheck.gone()
        }
        itemView.setOnclick {
            if (data.isInSelectMode) {
                data.isSelected = !data.isSelected
                binding.iconCheck.isSelected = data.isSelected
            }
            onItemClicked?.invoke(it, data, position)
        }
    }

}