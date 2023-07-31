package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

class MyOtherCommentData {
    val list = ArrayList<Data>()
    val isFirstPage = false
    val isLastPage = false
    class Data: BaseListData(LayoutTypes.TYPE_OTHER_COMMENT_MINE){
        var id = 0
        var title = ""
        var content = ""
        var dealerId = 0
        var coverPicture = ""
        var createTime = 0L
        var recreateTime = 0L
        var createUser = ""
        var model = ""
    }
}