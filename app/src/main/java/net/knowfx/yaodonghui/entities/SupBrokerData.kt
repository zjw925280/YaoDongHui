package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

data class SupBrokerData(

    val list:ArrayList<ItemData> = ArrayList(),
    val isFirstPage: Boolean = false,
    val isLastPage: Boolean = false,
)

data class ItemData(
    val id: Int = 0,
    val label: String = "",
    val grade: String = "",
    val fullName: String = "",
    val lableNames: ArrayList<String> = ArrayList(),
    val num: String = "",
    val logofile: String = ""
): BaseListData(LayoutTypes.TYPE_SUPERVISE_DEALER)
