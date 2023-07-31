package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.LayoutItemCommentBinding
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.entities.CommentData
import net.knowfx.yaodonghui.ext.checkIsLogin
import net.knowfx.yaodonghui.ext.getCreateFormatTime
import net.knowfx.yaodonghui.ext.getUserData
import net.knowfx.yaodonghui.ext.intoCircle
import net.knowfx.yaodonghui.ext.setOnclick

class CommentHolder(parent: ViewGroup, id: Int) : BaseViewHolder(parent, id) {
    override fun onBind(
        data: BaseListData,
        position: Int,
        onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
    ) {
        data as CommentData.Data
        val binding = LayoutItemCommentBinding.bind(itemView)
        binding.userAvatar.intoCircle(data.userhead, R.mipmap.ic_avatar_default)
        binding.userName.text = data.nickname
        binding.content.text = data.content
        binding.commentTime.text = data.createTime.getCreateFormatTime()
        if (data.createUser == getUserData()?.id) {
            itemView.setOnLongClickListener {
                (itemView.context as BaseActivity).checkIsLogin {
                    onItemClicked?.invoke(it, data, position)
                }
                return@setOnLongClickListener false
            }
        }

    }
}