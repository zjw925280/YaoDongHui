package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.LayoutItemIndexFunctionBinding
import net.knowfx.yaodonghui.entities.AllFunctionList
import net.knowfx.yaodonghui.ext.intoCorners
import net.knowfx.yaodonghui.ext.setOnclick

class FunctionSuperviseHolder(parent: ViewGroup, resId: Int): BaseViewHolder(parent, resId) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as AllFunctionList.SuperviseFunctionData
        val binding = LayoutItemIndexFunctionBinding.bind(itemView)
        val corner = itemView.resources.getDimensionPixelOffset(R.dimen.dp_13)
        binding.funIv.intoCorners(data.logofile, corner.toFloat())
        binding.funTv.text = data.name
        itemView.setOnclick {
            onItemClicked?.invoke(it, data, position)
        }
    }
}