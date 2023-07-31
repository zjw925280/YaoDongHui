package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

class IndexHistoryBottomListData : BaseListData(LayoutTypes.TYPE_INDEX_HISTORY_BOTTOM) {

    val list = ArrayList<ArrayList<HistoryBottomItemData>>()

    class HistoryBottomItemData: BaseListData(LayoutTypes.TYPE_INDEX_HISTORY_BOTTOM_ITEM) {
        var logo = ""
        var id = 0
        var fullName = ""
    }
}