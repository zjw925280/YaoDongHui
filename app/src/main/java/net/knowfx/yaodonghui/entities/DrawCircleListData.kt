package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

class DrawCircleListData {
    val list = ArrayList<Data>()
    val isLastPage = false
    val isFirstPage = true

    class Data: BaseListData(LayoutTypes.TYPE_DRAW_CIRCLE_LIST){
        var id = 0
        var title = ""
        var userhead = ""
        var nickname = ""
        var createTime = 0L
        var coverPicture = ""
        var divide = ""
    }
}