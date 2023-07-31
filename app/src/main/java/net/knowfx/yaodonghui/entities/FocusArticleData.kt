package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes.TYPE_FOCUS_ARTICLE

class FocusArticleData {
    val isFirstPage = false
    val isLastPage = false
    val list = ArrayList<ItemData>()

    data class ItemData(
        val title: String = "",
        val model: String = "",
        val createTime: Long = 0L,
        val createUser: Int = 0,
        val userhead: String = "",
        val nickname: String = "",
        val logofile: String = "",
        val attentionId: Int = 0,
        val modelId: Int = 0
    ): BaseListData(TYPE_FOCUS_ARTICLE) {
        var isInSelectMode = false
        var isSelected = false
    }
}