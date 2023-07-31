package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

data class PushData(
    val id: Int = 0,
    val title: String = "",
    val content: String = "",
    val logofile: String = "",
    val createTime: String = "",
    val model: String = "",
    val buinessId: Int = 0,
): BaseListData(LayoutTypes.TYPE_PUSH_LIST){
    var isShowDate = false
    var date = ""
}