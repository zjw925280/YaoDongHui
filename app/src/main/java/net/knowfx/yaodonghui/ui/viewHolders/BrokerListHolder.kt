package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.LayoutItemBrokerBinding
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.entities.BrokerListData
import net.knowfx.yaodonghui.ext.getLabelStr
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.intoCorners
import net.knowfx.yaodonghui.ext.intoLogoOrCover
import net.knowfx.yaodonghui.ext.visible

class BrokerListHolder(parent: ViewGroup, id: Int) : BaseViewHolder(parent, id) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        val binding = LayoutItemBrokerBinding.bind(itemView)
        data as BrokerListData
        val corner = itemView.context.resources.getDimensionPixelOffset(R.dimen.dp_5)
        binding.brokerIcon.intoLogoOrCover(url = data.logo.ifEmpty { data.logofile }, corner = corner)
        binding.labelText.isSelected = data.label != "监管中"
        binding.labelText.text = data.label
        binding.brokerName.text = data.name.ifEmpty { data.fullName }
        binding.brokerMark.text = data.lableNames.getLabelStr()
        binding.brokerScore.text = data.grade.ifEmpty { "0.00" }
        if (data.rowNum < 0) {
            binding.brokerRankIcon.gone()
            binding.brokerWatch.visible()
            binding.brokerWatch.isSelected = true
        } else if (data.rowNum == 0) {
            binding.brokerRankIcon.gone()
            binding.brokerWatch.gone()
        } else {
            binding.brokerRankIcon.visible()
            binding.brokerWatch.gone()
            binding.brokerRankIcon.setImageLevel(if (data.isBlock) 3 else data.rowNum)
            binding.brokerRankIcon.setImageResource(
                when (data.rowNum) {
                    1 -> if (data.isBlock) R.mipmap.icon_rank_gray_1 else R.mipmap.icon_rank_1
                    2 -> if (data.isBlock) R.mipmap.icon_rank_gray_2 else R.mipmap.icon_rank_2
                    3 -> if (data.isBlock) R.mipmap.icon_rank_gray_3 else R.mipmap.icon_rank_3
                    else -> R.mipmap.icon_rank_default
                }
            )
            binding.brokerRankText.text = if (data.rowNum > 3) {
                data.rowNum.toString()
            } else {
                ""
            }
        }
        itemView.setOnClickListener {
            onItemClicked?.invoke(it, data, position)
        }
    }
}