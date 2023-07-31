package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.LayoutItemFocusDealerBinding
import net.knowfx.yaodonghui.entities.FocusBrokerData
import net.knowfx.yaodonghui.ext.getLabelStr
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.intoLogoOrCover
import net.knowfx.yaodonghui.ext.setOnclick
import net.knowfx.yaodonghui.ext.visible

class FocusDealerHolder(parent: ViewGroup, layoutId: Int) : BaseViewHolder(parent, layoutId) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as FocusBrokerData.ItemData
        val binding = LayoutItemFocusDealerBinding.bind(itemView)
        binding.layoutBroker.apply {
            val corner = itemView.resources.getDimensionPixelOffset(R.dimen.dp_5)
            brokerIcon.intoLogoOrCover(data.logofile, corner = corner)
            labelText.text = data.label
            labelText.isSelected = data.label != "监管中"
            brokerName.text = data.name.ifEmpty { data.fullName }
            brokerMark.text = data.lableNames.getLabelStr()
            brokerScore.text = data.grade.ifEmpty { "0.00" }
            brokerRankText.gone()
            brokerRankIcon.gone()
            brokerWatch.gone()
        }
        if (data.isSelectMode) {
            binding.iconCheck.visible()
            binding.iconCheck.isSelected = data.isSelected
        } else {
            binding.iconCheck.gone()
        }
        itemView.setOnclick {
            if (data.isSelectMode) {
                data.isSelected = !data.isSelected
                binding.iconCheck.isSelected = data.isSelected
            }
            onItemClicked?.invoke(it, data, position)
        }
    }
}