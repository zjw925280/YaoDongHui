package net.knowfx.yaodonghui.ui.viewHolders

import android.view.View
import android.view.ViewGroup
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.databinding.LayoutItemSuperviseBinding
import net.knowfx.yaodonghui.databinding.LayoutItemSuperviseCommentBinding
import net.knowfx.yaodonghui.databinding.LayoutItemSuperviseExploreBinding
import net.knowfx.yaodonghui.databinding.LayoutItemSuperviseNewsBinding
import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.entities.BrokerContentData
import net.knowfx.yaodonghui.entities.IndexPagerData
import net.knowfx.yaodonghui.ext.getCreateFormatTime
import net.knowfx.yaodonghui.ext.getTextFromModel
import net.knowfx.yaodonghui.ext.intoCircle
import net.knowfx.yaodonghui.ext.intoCorners
import net.knowfx.yaodonghui.ext.setOnclick

object BrokerContentViewHolders {
    class SuperviseHolder(parent: ViewGroup, id: Int) : BaseViewHolder(parent, id) {
        override fun onBind(
            data: BaseListData,
            position: Int,
            onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
        ) {
            data as BrokerContentData.SuperviseData
            val binding = LayoutItemSuperviseBinding.bind(itemView)
            binding.flagIv.intoCorners(
                data.flagPath,
                itemView.resources.getDimension(R.dimen.dp_2)
            )
            binding.superviseName.text = data.name
            binding.superviseStatus.text = data.label
            binding.superviseStatus.isSelected = data.label != "监管中"
            itemView.setOnclick {
                onItemClicked?.invoke(it, data, position)
            }

        }
    }

    class ExploreHolder(parent: ViewGroup, id: Int) : BaseViewHolder(parent, id) {
        override fun onBind(
            data: BaseListData,
            position: Int,
            onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
        ) {
            data as BrokerContentData.ExploreData
            val binding = LayoutItemSuperviseExploreBinding.bind(itemView)
            binding.titleTv.text = data.title
            binding.avatarIv.intoCircle(data.userhead)
            binding.nicknameTv.text = data.nickname
            binding.timeTv.text = data.createTime.getCreateFormatTime()
            //根据 [data.type] 修改类型显示的背景颜色和文字内容，颜色
            binding.statusTv.text = getTextFromModel(model = data.type)
            itemView.setOnclick { onItemClicked?.invoke(it, data, position) }
        }
    }

    class CommentHolder(parent: ViewGroup, id: Int) : BaseViewHolder(parent, id) {
        override fun onBind(
            data: BaseListData,
            position: Int,
            onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
        ) {
            data as BrokerContentData.CommentData
            val binding = LayoutItemSuperviseCommentBinding.bind(itemView)
            binding.titleTv.text = data.content
            binding.avatarIv.intoCircle(data.userhead)
            binding.timeTv.text = data.createTime.getCreateFormatTime()
            binding.nicknameTv.text = data.nickname
            itemView.setOnclick { onItemClicked?.invoke(it, data, position) }
        }
    }

    class NewsHolder(parent: ViewGroup, id: Int) : BaseViewHolder(parent, id) {
        override fun onBind(
            data: BaseListData,
            position: Int,
            onItemClicked: ((view: View, data: BaseListData, position: Int) -> Unit)?
        ) {
            data as IndexPagerData.ListData
            val binding = LayoutItemSuperviseNewsBinding.bind(itemView)
            binding.titleTv.text = data.title
            binding.nameTimeTv.text = itemView.resources.getString(
                R.string.string_text_supervise_news_name_time,
                data.createUser,
                data.createTime.getCreateFormatTime()
            )
            binding.thumbIv.intoCorners(
                data.coverPicture,
                itemView.resources.getDimension(R.dimen.dp_5)
            )
            //根据 [data.type] 修改标识的背景颜色，文字
            binding.labelTv.text = getTextFromModel(model = data.flag)
            itemView.setOnclick { onItemClicked?.invoke(it, data, position) }
        }
    }
}