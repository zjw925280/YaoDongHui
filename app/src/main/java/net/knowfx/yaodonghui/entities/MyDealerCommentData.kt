package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

class MyDealerCommentData {
    val list = ArrayList<Data>()
    val isFirstPage = false
    val isLastPage = false

    class Data : BaseListData(LayoutTypes.TYPE_DEALER_COMMENT_MINE) {
        var id: Int = 0 // id
        var title: String = "" // 交易商评论标题
        var content: String = "" // 交易商评论内容
        var dealerId: String = "" // 交易商ID
        var photo: String = "" // 图片
        var name: String = "" // 交易商简称
        var createTime: Long = 0L // 发布时间
    }
}