package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

data class MessageList(
    val total: Int = 0,
    val isLastPage: Boolean = false,
    val isFirstPage: Boolean = false,
    val list: ArrayList<MessageData> = arrayListOf()
)
data class MessageData(
    val title: String = "",
    val content: String = "",
    val create_time: Long = 0L,
    val createTime: Long = 0L,
) : BaseListData(LayoutTypes.TYPE_MSG_LIST)
