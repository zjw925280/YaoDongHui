package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.LayoutItemBrandEventBinding
import net.knowfx.yaodonghui.entities.BrandEventData
import net.knowfx.yaodonghui.ext.formatTime
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter

class BrandEventHolder(parent: ViewGroup, layoutId: Int) : BaseViewHolder(parent, layoutId) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as BrandEventData
        val binding = LayoutItemBrandEventBinding.bind(itemView)
        binding.tvTime.text = data.createTime.formatTime()
        binding.tvTitle.text = data.title
        binding.tvContent.setHtml(data.content, HtmlHttpImageGetter(binding.tvContent))
    }
}