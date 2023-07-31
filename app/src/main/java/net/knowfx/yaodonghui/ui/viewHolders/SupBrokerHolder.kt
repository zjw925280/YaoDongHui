package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.LayoutItemSupDealerBinding
import net.knowfx.yaodonghui.entities.ItemData
import net.knowfx.yaodonghui.ext.getLabelStr
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.intoLogoOrCover
import net.knowfx.yaodonghui.ext.setOnclick

class SupBrokerHolder(parent: ViewGroup, layoutId: Int) : BaseViewHolder(parent, layoutId) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as ItemData
        val binding = LayoutItemSupDealerBinding.bind(itemView)
        binding.brokerLayout.apply {
            val corner = itemView.resources.getDimensionPixelOffset(R.dimen.dp_5)
            brokerIcon.intoLogoOrCover(data.logofile, corner = corner)
            brokerName.text = data.fullName
            labelText.text = data.label
            labelText.isSelected = data.label != "监管中"
            brokerScore.text = data.grade.ifEmpty { "0.00" }
            brokerMark.text = data.lableNames.getLabelStr()
            brokerRankIcon.gone()
            brokerRankText.gone()
            brokerWatch.gone()
        }
        binding.supCode.text = itemView.context.getString(R.string.string_supervise_num, data.num)
        itemView.setOnclick { onItemClicked?.invoke(it, data, position) }
    }
}