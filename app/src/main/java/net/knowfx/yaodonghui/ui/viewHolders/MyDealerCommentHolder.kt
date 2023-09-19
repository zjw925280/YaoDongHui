package net.knowfx.yaodonghui.ui.viewHolders

import android.util.Log
import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.databinding.LayoutItemMyPostBinding
import net.knowfx.yaodonghui.entities.MyDealerCommentData
import net.knowfx.yaodonghui.ext.getCreateFormatTime
import net.knowfx.yaodonghui.ext.gone
import net.knowfx.yaodonghui.ext.intoCorners

class MyDealerCommentHolder(parent: ViewGroup, layoutId: Int): BaseViewHolder(parent, layoutId) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as MyDealerCommentData.Data
        val binding = LayoutItemMyPostBinding.bind(itemView)
        val corner = itemView.resources.getDimensionPixelOffset(R.dimen.dp_5)
        binding.thumbIv.intoCorners(url = data.photo, radius = corner.toFloat())
        binding.thumbIv.setOnClickListener {
            Log.e("点击了","点击了");
        }
        binding.titleTv.text = data.title
        binding.contentTv.text = data.content
        binding.labelTv.gone()
        binding.textDealer.text =
            itemView.context.getString(R.string.string_my_post_dealer, data.name)
        binding.textTime.text = data.createTime.getCreateFormatTime()
    }
}