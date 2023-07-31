package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.LayoutItemIndexHistoryTopBinding
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.entities.IndexHistoryTopData
import net.knowfx.yaodonghui.ext.intoCorners
import net.knowfx.yaodonghui.ext.intoWithSize
import net.knowfx.yaodonghui.ext.setOnclick

class IndexHistoryTopHolder(parent: ViewGroup, id: Int) : BaseViewHolder(parent, id) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as IndexHistoryTopData.IndexHistoryTopItemData
        val binding = LayoutItemIndexHistoryTopBinding.bind(itemView)
        val corner = itemView.context.resources.getDimension(R.dimen.dp_4)
        val resources = itemView.context.resources
        val gaps =
            (2 * resources.getDimensionPixelOffset(R.dimen.dp_12) + (3 * resources.getDimensionPixelOffset(
                R.dimen.dp_5
            )))
        val width = (resources.displayMetrics.widthPixels - gaps) / 4
        val height = width / 1.618
        val param = binding.copLogo.layoutParams
        param.width = LayoutParams.MATCH_PARENT
        param.height = height.toInt()
        binding.copLogo.layoutParams = param
        binding.copLogo.measure(0, 0)
        binding.copLogo.intoWithSize(
            data.logo,
            width = width,
            height = height.toInt(),
            corner = corner
        )
        itemView.setOnclick {
            onItemClicked?.invoke(it, data, position)
        }
    }
}