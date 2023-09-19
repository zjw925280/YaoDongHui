package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.LayoutItemPushBinding
import net.knowfx.yaodonghui.entities.PushData
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.intoLogoOrCover
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.visible

class PushHolder(parent: ViewGroup, layoutId: Int) : BaseViewHolder(parent, layoutId) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        val binding = LayoutItemPushBinding.bind(itemView)
        (data as PushData).apply {
            val height = itemView.resources.getDimensionPixelOffset(R.dimen.dp_40)
            val corner = itemView.resources.getDimensionPixelOffset(R.dimen.dp_5)
            if(logofile!=null&&!logofile.equals("")){
                binding.ivThumb.intoLogoOrCover(url = logofile, height = height, corner = corner)
            }else{
                binding.ivThumb.gone()
            }

            binding.textTitle.text = title
            binding.textContent.text = content
            if (isShowDate) {
                binding.textDate.text = date
                binding.textDate.visible()
            } else {
                binding.textDate.gone()
            }
        }
        itemView.setOnclick { onItemClicked?.invoke(it,data, position) }
    }
}