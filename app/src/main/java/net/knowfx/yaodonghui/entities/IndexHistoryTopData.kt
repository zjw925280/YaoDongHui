package net.knowfx.yaodonghui.entities

import net.knowfx.yaodonghui.base.BaseListData
import net.knowfx.yaodonghui.utils.LayoutTypes

class IndexHistoryTopData : BaseListData(LayoutTypes.TYPE_INDEX_HISTORY_TOP) {

    var list = ArrayList<IndexHistoryTopItemData>()

    class IndexHistoryTopItemData : BaseListData(LayoutTypes.TYPE_INDEX_HISTORY_TOP_ITEM) {
        var logo = ""
        var id = 0
    }
}