package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import coil.load
import net.knowfx.yaodonghui.databinding.LayoutItemIndexFunctionBinding
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.entities.IndexFunctionListData
import net.knowfx.yaodonghui.ext.setOnclick

class IndexFunctionHolder(parent: ViewGroup, resId: Int) : BaseViewHolder(parent, resId) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as IndexFunctionListData.IndexFunctionData
        val binding = LayoutItemIndexFunctionBinding.bind(itemView)
        binding.funIv.load(data.photo)
        binding.funTv.text = data.bannerName
        itemView.setOnclick {
            onItemClicked?.invoke(itemView, data, position)
        }
    }

}