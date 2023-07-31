package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.LayoutItemClassBinding
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.entities.ClassListData
import net.knowfx.yaodonghui.ext.getCreateFormatTime
import net.knowfx.yaodonghui.ext.into
import net.knowfx.yaodonghui.ext.intoCircle
import net.knowfx.yaodonghui.ext.setMultipleClick

class ClassListHolder(parent: ViewGroup, id: Int) : BaseViewHolder(parent, id) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as ClassListData.Data
        val binding = LayoutItemClassBinding.bind(itemView)
        binding.titleTv.text = data.title
        binding.headIv.intoCircle(data.userhead)
        binding.nameTv.text = data.nickname
        binding.timeTv.text = data.createTime.getCreateFormatTime()
        binding.btnShare.setIconAndText(
            R.mipmap.btn_share,
            itemView.resources.getString(R.string.string_btn_share)
        )
        binding.btnComment.setIconAndText(R.mipmap.btn_all_comment, data.totalComment.toString())
        binding.btnFocus.setIconAndText(
            R.drawable.selector_comment_focus,
            itemView.resources.getString(if (data.follow) R.string.string_text_focus_s else R.string.string_text_focus)
        )
        binding.btnFocus.isSelected = data.follow
        binding.videoPlayer.into(data.coverPicture)
        setMultipleClick(binding.btnShare, binding.btnComment, binding.btnFocus, itemView) {
            onItemClicked?.invoke(it, data, position)
        }

    }
}