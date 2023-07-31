package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

class ClassListData {
    val isFirstPage = false
    val isLastPage = false
    val list = ArrayList<Data>()
    class Data : BaseListData(LayoutTypes.TYPE_CLASS_LIST) {
        val id = 0
        val coverPicture = ""
        val title = ""
        val createUser = ""
        val createTime = 0L
        val nickname = ""
        val userhead = ""
        val videoUrl = ""
        val totalComment = 0
        var follow = false
    }
}